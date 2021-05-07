package firenze.poker

import Game

interface Action {
    fun execute(player: Player, game: Game)
}

class Bet: Action {
    override fun execute(player: Player, game: Game) {
        game.betExecute(player)
    }

}

class Call: Action {
    override fun execute(player: Player, game: Game) {
        game.callExecute(player)
    }
}
class Check: Action {
    override fun execute(player: Player, game: Game) {
        game.checkExecute(player)
    }
}

class Fold: Action {
    override fun execute(player: Player, game: Game) {
        game.foldExecute(player)
    }
}
class Raise: Action {
    override fun execute(player: Player, game: Game) {
        game.raiseExecute(player)
    }
}