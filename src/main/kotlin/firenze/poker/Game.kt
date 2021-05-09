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
                shutDown()
            }
        }
    }

    private fun shutDown() {
        end = true
    }

    private fun prepareForNextRound(){
        currentRoundName = Rounds.values()[currentRoundName.ordinal + 1]
        val activePlayers = players.filter { it.isActive }.toTypedArray()
        round = Round(*activePlayers)
    }
}