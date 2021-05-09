package firenze.poker

import Round
import firenze.poker.enums.Rounds

class Game(private vararg val players: Player) {
    val round = Round(*players)
    var currentRoundName: Rounds = Rounds.PREFLOP

    fun execute(action: Action){
        round.execute(action)
        if (round.nextRound()){
            currentRoundName = Rounds.values()[currentRoundName.ordinal + 1]
        }
    }
}