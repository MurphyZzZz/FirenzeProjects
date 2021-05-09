
import firenze.poker.Action
import firenze.poker.Player
import firenze.poker.enums.Rounds

class Round(private vararg val players: Player) {

    var round: Rounds = Rounds.PREFLOP
    var currentBid: Int = 0
    private var minimumWager: Int = 1
    var pot: Int = 0
    val waitingPlayers = mutableListOf(*players)
    private val hasDonePlayers = mutableSetOf<Player>()

    fun execute(action: Action) {
        val activePlayer = waitingPlayers.removeFirst()
        action.execute(activePlayer, this)
        nextRound()
    }

    fun betExecute(player: Player) {
        currentBid = minimumWager
        pot += currentBid
        player.calculateMoney(currentBid)
        waitingPlayers.add(player)
        hasDonePlayers.add(player)
    }

    fun callExecute(player: Player) {
        pot += (currentBid - player.currentRoundBid)
        player.calculateMoneyForCall(currentBid)
        waitingPlayers.add(player)
        hasDonePlayers.add(player)
    }

    fun foldExecute(player: Player) {
        player.isActive = false
        hasDonePlayers.add(player)
    }

    fun raiseExecute(player: Player) {
        val bid = player.getRaiseWager()
        currentBid = bid
        pot += bid
        player.calculateMoney(currentBid)
        waitingPlayers.add(player)
        hasDonePlayers.add(player)
    }

    fun checkExecute(player: Player) {
        waitingPlayers.add(player)
        hasDonePlayers.add(player)
    }

    fun allInExecute(player: Player) {
        val bid = player.getAllInWager()
        currentBid = if (bid > currentBid) bid else currentBid
        pot += bid
        player.calculateMoney(currentBid)
        player.isActive = false
        player.isAllIn = true
        hasDonePlayers.add(player)
    }

    private fun nextRound() {
        val wagerOfHasDonePlayers = hasDonePlayers.filter { it.isActive }.map { it.currentRoundBid }

        val allActivePlayers: () -> Set<Player> = { players.filter { it.isActive }.toSet() }
        val numberOfActivePlayers = allActivePlayers().size

        if (wagerOfHasDonePlayers.all { it == currentBid } && (hasDonePlayers.size == numberOfActivePlayers)) {
            round = Rounds.values()[round.ordinal + 1]
        }
    }
}