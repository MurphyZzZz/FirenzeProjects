package firenze.poker.service

import firenze.poker.exception.InvalidNumberOfPlays
import firenze.poker.model.Play
import firenze.poker.model.PokerGame

class PokerGameService {
    fun start(plays: List<Play>): PokerGame {
        if (plays.size < 2 || plays.size > 10) throw InvalidNumberOfPlays()
        return PokerGame(plays = plays)
    }
}