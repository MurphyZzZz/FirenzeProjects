import firenze.poker.Action
import firenze.poker.Player
import firenze.poker.enums.Round

class Game(private vararg val players: Player) {

    var round: Round = Round.PREFLOP
    var currentBid: Int = 0
    var minimumWager: Int = 1
    var pot: Int = 0
    val waitingPlayers = mutableListOf(*players)
    val activePlayers = mutableListOf(*players)
    val allInPlayers = mutableListOf<Player>()
    val hasDonePlayersAndWager = players.associate { it to 0 }.toMutableMap()

    fun execute(action: Action) {
        val activePlayer = waitingPlayers.removeFirst()
        action.execute(activePlayer, this)
        nextRound()
    }

    fun betExecute(player: Player) {
        currentBid = minimumWager
        pot += currentBid
        hasDonePlayersAndWager[player] = hasDonePlayersAndWager[player]!! + currentBid
        waitingPlayers.add(player)
    }

    fun callExecute(player: Player) {
        pot += (currentBid - hasDonePlayersAndWager[player]!!)
        hasDonePlayersAndWager[player] = currentBid
        waitingPlayers.add(player)
    }

    fun foldExecute(player: Player) {
        activePlayers.remove(player)
    }

    fun raiseExecute(player: Player) {
        val bid = player.getRaiseWager()
        currentBid = bid
        pot += bid
        hasDonePlayersAndWager[player] = hasDonePlayersAndWager[player]!! + bid
        waitingPlayers.add(player)
    }

    fun checkExecute(player: Player) {
        waitingPlayers.add(player)
    }

    fun allInExecute(player: Player) {
        val bid = player.getAllInWager()
        currentBid = if (bid > currentBid) bid else currentBid
        pot += bid
        hasDonePlayersAndWager[player] = hasDonePlayersAndWager[player]!! + bid
        activePlayers.remove(player)
        allInPlayers.add(player)
    }

    private fun nextRound(){
        val wagerOfHasDonePlayers = hasDonePlayersAndWager.keys.filter { activePlayers.contains(it) }.map { hasDonePlayersAndWager[it] }
        if (wagerOfHasDonePlayers.all { it == currentBid } && (hasDonePlayersAndWager.keys.size == activePlayers.size)){
            round = Round.values()[round.ordinal + 1]
        }
    }
}