import firenze.poker.Player
import firenze.poker.enums.Round

class Game(vararg val players: Player) {

    var round: Round = Round.PREFLOP
    var currentBid: Int = 0
    var minimumWager: Int = 1
    var pot: Int = 0
    val waitingPlayers = mutableListOf(*players)
    val activePlayers = mutableListOf(*players)
    val hasDonePlayersAndWager = players.associate { it to 0 }.toMutableMap()

    fun bet() {
        val player = waitingPlayers.removeFirst()
        currentBid = minimumWager
        pot += currentBid
        waitingPlayers.add(player)
        hasDonePlayersAndWager[player] = hasDonePlayersAndWager[player]!! + currentBid
    }

    fun call() {
        val player = waitingPlayers.removeFirst()
        pot += (currentBid - hasDonePlayersAndWager[player]!!)
        waitingPlayers.add(player)
        hasDonePlayersAndWager[player] = currentBid
        nextRound()
    }

    fun fold() {
        val player = waitingPlayers.removeFirst()
        activePlayers.remove(player)
        hasDonePlayersAndWager.remove(player)
        nextRound()
    }

    fun check() {
        val player = waitingPlayers.removeFirst()
        waitingPlayers.add(player)
        nextRound()
    }

    fun raise(bid: Int) {
        val player = waitingPlayers.removeFirst()
        currentBid = bid
        pot += bid
        waitingPlayers.add(player)
        hasDonePlayersAndWager[player] = hasDonePlayersAndWager[player]!! + bid
        nextRound()
    }

    fun nextRound(){
        if (hasDonePlayersAndWager.all { it.value == currentBid } && (hasDonePlayersAndWager.keys.size == activePlayers.size)){
            round = Round.values()[round.ordinal + 1]
        }
    }
}