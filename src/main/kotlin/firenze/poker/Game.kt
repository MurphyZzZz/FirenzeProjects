package firenze.poker

import Round
import firenze.poker.enums.Rounds

class Game(private vararg val players: Player) {
    var round = Round(*players)
    var currentRoundName: Rounds = Rounds.PREFLOP
    var end = false

    fun execute(action: Action){
        round.execute(action)
        if (round.nextRound()){
            val nextRoundIndex = currentRoundName.ordinal + 1
            if (nextRoundIndex < Rounds.values().size){
                prepareForNextRound()
            } else {
                endGame()
            }
        }
    }

    private fun endGame(){
        val winners = shutDown()
        splitPot(winners)
    }

    private fun splitPot(winners: List<Player>) {
    }

    private fun shutDown(): List<Player> {
        end = true
        return emptyList()
    }

    private fun prepareForNextRound(){
        currentRoundName = Rounds.values()[currentRoundName.ordinal + 1]
        val activePlayers = players.filter { it.isActive }.toTypedArray()
        if (activePlayers.size < 2){
            endGame()
        }
        round = Round(*activePlayers)
    }
}