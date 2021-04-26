package firenze.poker.service

import firenze.poker.exception.InvalidNumberOfPlays
import firenze.poker.model.Play
import firenze.poker.model.PokerGame

class PokerGameService {
    fun start(plays: List<Play>): PokerGame {
        isValidNumberOfPlays(plays)

        return PokerGame(plays = plays)
    }

    private fun isValidNumberOfPlays(plays: List<Play>) {
        // TODO: need to figure out if the number of plays is equals to 2, how to handle bigBlind ?
        if (plays.size < 3 || plays.size > 10) throw InvalidNumberOfPlays()
    }
}