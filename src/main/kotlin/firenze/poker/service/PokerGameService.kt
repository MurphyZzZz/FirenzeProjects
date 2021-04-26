package firenze.poker.service

import firenze.poker.exception.InvalidNumberOfPlays
import firenze.poker.model.Player
import firenze.poker.model.PokerGame

class PokerGameService {
    fun start(players: List<Player>): PokerGame {
        isValidNumberOfPlays(players)

        return PokerGame(players = players)
    }

    private fun isValidNumberOfPlays(players: List<Player>) {
        // TODO: need to figure out if the number of plays is equals to 2, how to handle bigBlind ?
        if (players.size < 3 || players.size > 10) throw InvalidNumberOfPlays()
    }
}