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
        splitPot(winners)
    }

    private fun splitPot(winnerSets: List<Set<Player>>) {
        // if there only one winner
        val winners = winnerSets.flatten()
        if (winners.size == 1){
            val playersForOneSet = winnerSets.first()
            val player = playersForOneSet.first()
            player.money += (totalWager - player.totalBid)
        }
        // if more than one winner: A > B > C
        // if more than one winner with same cards
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
            round = Round(*activePlayers)
        }
    }
}