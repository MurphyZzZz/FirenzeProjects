package firenze.poker

import Round

interface Action {
    fun execute(player: Player, round: Round)
}

class Bet: Action {
    override fun execute(player: Player, round: Round) {
        round.betExecute(player)
    }

}

class Call: Action {
    override fun execute(player: Player, round: Round) {
        round.callExecute(player)
    }
}
class Check: Action {
    override fun execute(player: Player, round: Round) {
        round.checkExecute(player)
    }
}

class Fold: Action {
    override fun execute(player: Player, round: Round) {
        round.foldExecute(player)
    }
}

class Raise: Action {
    override fun execute(player: Player, round: Round) {
        round.raiseExecute(player)
    }
}

class AllIn: Action {
    override fun execute(player: Player, round: Round) {
        round.allInExecute(player)
    }
}