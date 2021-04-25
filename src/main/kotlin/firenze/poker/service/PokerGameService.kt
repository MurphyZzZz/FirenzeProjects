package firenze.poker.service

import firenze.poker.exception.InvalidNumberOfPlays
import firenze.poker.model.Play
import firenze.poker.model.PokerGame
import firenze.poker.model.Pot
import firenze.poker.model.Round

class PokerGameService {
    fun start(plays: List<Play>): PokerGame {
        // TODO: need to figure out if the number of plays is equals to 2, how to handle bigBlind ?
        if (plays.size < 3 || plays.size > 10) throw InvalidNumberOfPlays()
        val round = Round("Pre-flop")
        val pot = Pot(0)
        return PokerGame(
            plays = plays,
            round = round,
            pot = pot,
            communityCards = emptyList(),
            button = plays[0],
            smallBlind = plays[1],
            bigBlind = plays[2]
        )
    }
}