package firenze.poker.fixture

import firenze.poker.model.Card
import firenze.poker.model.Player
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

    fun plays(): List<Player> {
        val play1 = Player("Lisa", amounts = 10)
        val play2 = Player("Lisa", amounts = 10)
        val play3 = Player("Aaron", amounts = 10)
        return listOf(play1, play2, play3)
    }

    fun cardsForPlay(): List<Card> {
        val card1 = Card(1, 1)
        val card2 = Card(2, 1)
        return listOf(card1, card2)
    }

    fun button(): Player {
        return plays()[0]
    }

    fun smallBlind(): Player {
        return plays()[1]
    }

    fun bigBlind(): Player {
        return plays()[2]
    }
}