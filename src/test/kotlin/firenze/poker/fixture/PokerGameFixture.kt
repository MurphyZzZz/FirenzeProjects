package firenze.poker.fixture

import firenze.poker.model.Card
import firenze.poker.model.Play
import firenze.poker.model.PokerGame
import firenze.poker.model.Pot
import firenze.poker.model.Round

object PokerGameFixture {
    fun pokerGame(): PokerGame {
        return PokerGame(
            plays()
        )
    }

    fun pot(): Pot {
        return Pot(0)
    }

    fun round(): Round {
        return Round("Pre-flop")
    }

    fun plays(): List<Play> {
        val play1 = Play("Lisa", amounts = 10, cards = cardsForPlay())
        val play2 = Play("Lisa", amounts = 10, cards = cardsForPlay())
        val play3 = Play("Aaron", amounts = 10, cards = cardsForPlay())
        return listOf(play1, play2, play3)
    }

    fun cardsForPlay(): List<Card> {
        val card1 = Card(1, 1)
        val card2 = Card(2, 1)
        return listOf(card1, card2)
    }

    fun button(): Play {
        return plays()[0]
    }

    fun smallBlind(): Play {
        return plays()[1]
    }

    fun bigBlind(): Play {
        return plays()[2]
    }
}