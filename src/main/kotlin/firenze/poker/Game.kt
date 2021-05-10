package firenze.poker

import Round
import firenze.poker.enums.Rounds

class Game(private vararg val players: Player) {
    var round = Round(*players)
    var currentRoundName: Rounds = Rounds.PREFLOP
    var end = false
    var totalWager = 0

    fun execute(action: Action){
        round.execute(action)
        if (round.nextRound()){
            prepareForNextRound()
        }
    }

    private fun endGame(){
        end = true
        val winners = shutDown()
        cutPlayerBid()
        splitPot(winners)
    }

    private fun cutPlayerBid() {
        players.forEach { player -> player.money -= player.totalBid }
    }

    private fun splitPot(winnerSets: List<Set<Player>>) {
        // if there only one winner
        val winners = winnerSets.flatten()
        if (winners.size == 1){
            val playersForOneSet = winnerSets.first()
            val player = playersForOneSet.first()
            player.money += totalWager
            return
        }
        // if more than one winner: A > B > C
        // if more than one winner with same cards
        winnerSets.forEach { calculateWagerInSidePot(it) }
    }

    private fun calculateWagerInSidePot(winners: Set<Player>){
        if (totalWager <= 0) {
            return
        }

        if (winners.size == 1){
            val player = winners.first()
            if ((player.totalBid * players.size) <= totalWager) {
                val wager = player.totalBid * players.size
                player.money += wager
                totalWager -= wager
            } else {
                player.money += totalWager
                totalWager = 0
            }
        } else {
            val player = winners.first()
            if ((player.totalBid * players.size) <= totalWager){
                winners.forEach { it.money += (player.totalBid * players.size / 2) }
                totalWager -= (player.totalBid * players.size)
            } else {
                val eachCut = totalWager / winners.size
                winners.forEach { it.money += eachCut }
                totalWager = 0
            }
        }
    }

    private fun shutDown(): List<Set<Player>> {
        return emptyList()
    }

    private fun prepareForNextRound(){
        totalWager += round.pot

        val nextRoundIndex = currentRoundName.ordinal + 1

        val activePlayers = players.filter { it.isActive }.toTypedArray()

        if ((nextRoundIndex >= Rounds.values().size) || (activePlayers.size < 2)){
            endGame()
        } else {
            currentRoundName = Rounds.values()[nextRoundIndex]
            activePlayers.forEach { it.currentRoundBid = 0 }
            round = Round(*activePlayers)
        }
    }
}