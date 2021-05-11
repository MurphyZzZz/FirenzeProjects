package firenze.poker.utils

import firenze.poker.domain.BestCombination
import firenze.poker.domain.Card
import firenze.poker.domain.Player
import firenze.poker.enums.Combinations
import kotlin.test.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class CardCalculatorTest {

    private lateinit var playerA: Player
    private lateinit var playerB: Player
    private lateinit var playerC: Player

    @BeforeEach
    internal fun setUp() {
        playerA = Player("A", 10)
        playerB = Player("B", 10)
        playerC = Player("C", 10)
    }

    @Test
    fun `should return -1 when combinations not equal given two cards`() {
        // given
        val card1 = Card(rank = 12, suit = 1)
        val card4 = Card(rank = 4, suit = 1)
        val card5 = Card(rank = 5, suit = 0)
        val card6 = Card(rank = 6, suit = 0)
        val card7 = Card(rank = 0, suit = 1)
        val cardsA = listOf(card7, card1, card6, card5, card4)
        val combinationA = BestCombination(Combinations.HIGH_CARD, cardsA)

        val card8 = Card(rank = 12, suit = 1)
        val card9 = Card(rank = 1, suit = 0)
        val card10 = Card(rank = 1, suit = 1)
        val card13 = Card(rank = 6, suit = 0)
        val card14 = Card(rank = 0, suit = 1)
        val cardsB = listOf(card9, card10, card14, card13, card8)
        val combinationB = BestCombination(Combinations.ONE_PAIR, cardsB)

        // when
        val result = CardCalculator.compareCards(combinationA, combinationB)

        // then
        assertEquals(-1, result)
    }

    @Test
    fun `should return -1 when combinations equal given two cards`() {
        // given
        val card1 = Card(rank = 12, suit = 1)
        val card4 = Card(rank = 1, suit = 1)
        val card5 = Card(rank = 1, suit = 0)
        val card6 = Card(rank = 6, suit = 0)
        val card7 = Card(rank = 7, suit = 1)
        val cardsA = listOf(card7, card1, card6, card5, card4)
        val combinationA = BestCombination(Combinations.ONE_PAIR, cardsA)

        val card8 = Card(rank = 12, suit = 1)
        val card9 = Card(rank = 1, suit = 0)
        val card10 = Card(rank = 1, suit = 1)
        val card13 = Card(rank = 6, suit = 0)
        val card14 = Card(rank = 0, suit = 1)
        val cardsB = listOf(card9, card10, card14, card13, card8)
        val combinationB = BestCombination(Combinations.ONE_PAIR, cardsB)

        // when
        val result = CardCalculator.compareCards(combinationA, combinationB)

        // then
        assertEquals(-1, result)
    }

    @Test
    fun `should return 0 given two same cards`() {
        // given
        val card1 = Card(rank = 12, suit = 1)
        val card4 = Card(rank = 1, suit = 1)
        val card5 = Card(rank = 1, suit = 0)
        val card6 = Card(rank = 6, suit = 0)
        val card7 = Card(rank = 0, suit = 1)
        val cardsA = listOf(card7, card1, card6, card5, card4)
        val combinationA = BestCombination(Combinations.ONE_PAIR, cardsA)

        val card8 = Card(rank = 12, suit = 1)
        val card9 = Card(rank = 1, suit = 0)
        val card10 = Card(rank = 1, suit = 1)
        val card13 = Card(rank = 6, suit = 0)
        val card14 = Card(rank = 0, suit = 1)
        val cardsB = listOf(card9, card10, card14, card13, card8)
        val combinationB = BestCombination(Combinations.ONE_PAIR, cardsB)

        // when
        val result = CardCalculator.compareCards(combinationA, combinationB)

        // then
        assertEquals(0, result)
    }

    @Test
    fun `should return winners given cards not equal`() {
        // given
        val card5 = Card(rank = 4, suit = 1)
        val card6 = Card(rank = 5, suit = 0)
        val card7 = Card(rank = 6, suit = 0)
        val communityCards = listOf(card5, card6, card7)

        val card1 = Card(rank = 2, suit = 1)
        val card4 = Card(rank = 3, suit = 1)
        playerA.cards.addAll(listOf(card1, card4))

        val card9 = Card(rank = 1, suit = 0)
        val card10 = Card(rank = 1, suit = 1)
        playerB.cards.addAll(listOf(card9, card10))

        val card15 = Card(rank = 12, suit = 1)
        val card19 = Card(rank = 0, suit = 1)
        playerC.cards.addAll(listOf(card15, card19))

        // when
        val result = CardCalculator.getWinners(listOf(playerA, playerB, playerC), communityCards)

        // then
        assertEquals(listOf(setOf(playerA), setOf(playerB), setOf(playerC)), result)
    }

    @Test
    fun `should return winners given some cards equal`() {
        // given
        val card5 = Card(rank = 12, suit = 1)
        val card6 = Card(rank = 6, suit = 0)
        val card7 = Card(rank = 0, suit = 1)
        val communityCards = listOf(card5, card6, card7)

        val card1 = Card(rank = 1, suit = 0)
        val card2 = Card(rank = 1, suit = 1)
        playerA.cards.addAll(listOf(card1, card2))

        val card9 = Card(rank = 1, suit = 0)
        val card10 = Card(rank = 1, suit = 1)
        playerB.cards.addAll(listOf(card9, card10))

        val card15 = Card(rank = 4, suit = 1)
        val card19 = Card(rank = 5, suit = 1)
        playerC.cards.addAll(listOf(card15, card19))

        // when
        val result = CardCalculator.getWinners(listOf(playerA, playerB, playerC), communityCards)

        // then
        assertEquals(listOf(setOf(playerA, playerB), setOf(playerC)), result)
    }
}