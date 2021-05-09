
import firenze.poker.Action
import firenze.poker.Player

class Round(private vararg val players: Player) {

    var currentBid: Int = 0
    private var minimumWager: Int = 1
    var pot: Int = 0
    val waitingPlayers = mutableListOf(*players)
    private val hasDonePlayers = mutableSetOf<Player>()

    fun execute(action: Action) {
        val activePlayer = waitingPlayers.removeFirst()
        action.execute(activePlayer, this)
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

    fun nextRound(): Boolean {

        val allActivePlayers: () -> Set<Player> = { players.filter { it.isActive }.toSet() }
        val wagerOfActivePlayers = allActivePlayers().map { it.currentRoundBid }

        if (wagerOfActivePlayers.all { it == currentBid } && hasDonePlayers.size == players.size) {
            return true
        }
        return false
    }
}