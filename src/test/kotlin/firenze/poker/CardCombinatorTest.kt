package firenze.poker

import firenze.poker.enums.Combinations
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

internal class CardCombinatorTest{

    @Test
    fun `should evaluate royal flush`() {
        // given
        val card1 = Card(rank = 0, suit = 1)
        val card2 = Card(rank = 1, suit = 1)
        val card3 = Card(rank = 9, suit = 1)
        val card4 = Card(rank = 10, suit = 1)
        val card5 = Card(rank = 11, suit = 1)
        val card6 = Card(rank = 12, suit = 1)
        val card7 = Card(rank = 2, suit = 1)
        val combinator = CardCombinator(setOf(card1, card2, card3, card4, card5, card6, card7))

        // when
        val result = combinator.evaluateHand()

        // then
        assertEquals(Combinations.ROYAL_FLUSH, result.first)
        assertEquals(listOf(card1, card3, card4, card5, card6), result.second)
    }

    @Test
    fun `should evaluate straight flush`() {
        // given
        val card1 = Card(rank = 1, suit = 2)
        val card2 = Card(rank = 8, suit = 1)
        val card3 = Card(rank = 9, suit = 1)
        val card4 = Card(rank = 10, suit = 1)
        val card5 = Card(rank = 11, suit = 1)
        val card6 = Card(rank = 12, suit = 1)
        val card7 = Card(rank = 2, suit = 1)
        val combinator = CardCombinator(setOf(card1, card2, card3, card4, card5, card6, card7))

        // when
        val result = combinator.evaluateHand()

        // then
        assertEquals(Combinations.STRAIGHT_FLUSH, result.first)
        assertEquals(listOf(card2, card3, card4, card5, card6), result.second)
    }

    @Test
    fun `should evaluate four of a kind`() {
        // given
        val card1 = Card(rank = 12, suit = 1)
        val card2 = Card(rank = 1, suit = 0)
        val card3 = Card(rank = 1, suit = 1)
        val card4 = Card(rank = 1, suit = 2)
        val card5 = Card(rank = 1, suit = 3)
        val card6 = Card(rank = 3, suit = 1)
        val card7 = Card(rank = 0, suit = 1)
        val combinator = CardCombinator(setOf(card1, card2, card3, card4, card5, card6, card7))

        // when
        val result = combinator.evaluateHand()

        // then
        assertEquals(Combinations.FOUR_OF_A_KIND, result.first)
        assertEquals(listOf(card2, card3, card4, card5, card7), result.second)
    }

    @Test
    fun `should evaluate full house`() {
        // given
        val card1 = Card(rank = 12, suit = 1)
        val card2 = Card(rank = 1, suit = 0)
        val card3 = Card(rank = 1, suit = 1)
        val card4 = Card(rank = 1, suit = 2)
        val card5 = Card(rank = 3, suit = 3)
        val card6 = Card(rank = 3, suit = 1)
        val card7 = Card(rank = 0, suit = 1)
        val combinator = CardCombinator(setOf(card1, card2, card3, card4, card5, card6, card7))

        // when
        val result = combinator.evaluateHand()

        // then
        assertEquals(Combinations.FULL_HOUSE, result.first)
        assertEquals(listOf(card2, card3, card4, card6, card5), result.second)
    }

    @Test
    fun `should evaluate flush`() {
        // given
        val card1 = Card(rank = 12, suit = 1)
        val card2 = Card(rank = 1, suit = 0)
        val card3 = Card(rank = 3, suit = 0)
        val card4 = Card(rank = 4, suit = 0)
        val card5 = Card(rank = 5, suit = 0)
        val card6 = Card(rank = 6, suit = 0)
        val card7 = Card(rank = 0, suit = 1)
        val combinator = CardCombinator(setOf(card1, card2, card3, card4, card5, card6, card7))

        // when
        val result = combinator.evaluateHand()

        // then
        assertEquals(Combinations.FLUSH, result.first)
        assertEquals(listOf(card2, card3, card4, card5, card6), result.second)
    }

    @Test
    fun `should evaluate straight`() {
        // given
        val card1 = Card(rank = 12, suit = 1)
        val card2 = Card(rank = 2, suit = 0)
        val card3 = Card(rank = 3, suit = 1)
        val card4 = Card(rank = 4, suit = 0)
        val card5 = Card(rank = 5, suit = 0)
        val card6 = Card(rank = 6, suit = 0)
        val card7 = Card(rank = 0, suit = 1)
        val combinator = CardCombinator(setOf(card1, card2, card3, card4, card5, card6, card7))

        // when
        val result = combinator.evaluateHand()

        // then
        assertEquals(Combinations.STRAIGHT, result.first)
        assertEquals(listOf(card2, card3, card4, card5, card6), result.second)
    }

    @Test
    fun `should evaluate three of a kind`() {
        // given
        val card1 = Card(rank = 12, suit = 1)
        val card2 = Card(rank = 4, suit = 0)
        val card3 = Card(rank = 4, suit = 1)
        val card4 = Card(rank = 4, suit = 2)
        val card5 = Card(rank = 5, suit = 2)
        val card6 = Card(rank = 6, suit = 0)
        val card7 = Card(rank = 0, suit = 1)
        val combinator = CardCombinator(setOf(card1, card2, card3, card4, card5, card6, card7))

        // when
        val result = combinator.evaluateHand()

        // then
        assertEquals(Combinations.THREE_OF_A_KIND, result.first)
        assertEquals(listOf(card2, card3, card4, card7, card1), result.second)
    }

    @Test
    fun `should evaluate two pair`() {
        // given
        val card1 = Card(rank = 12, suit = 1)
        val card2 = Card(rank = 1, suit = 0)
        val card3 = Card(rank = 1, suit = 1)
        val card4 = Card(rank = 3, suit = 0)
        val card5 = Card(rank = 3, suit = 1)
        val card6 = Card(rank = 6, suit = 0)
        val card7 = Card(rank = 0, suit = 1)
        val combinator = CardCombinator(setOf(card1, card2, card3, card4, card5, card6, card7))

        // when
        val result = combinator.evaluateHand()

        // then
        assertEquals(Combinations.TWO_PAIR, result.first)
        assertEquals(listOf(card4, card5, card2, card3, card7), result.second)
    }

    @Test
    fun `should evaluate one pair`() {
        // given
        val card1 = Card(rank = 12, suit = 1)
        val card2 = Card(rank = 1, suit = 0)
        val card3 = Card(rank = 1, suit = 1)
        val card4 = Card(rank = 4, suit = 0)
        val card5 = Card(rank = 5, suit = 0)
        val card6 = Card(rank = 6, suit = 0)
        val card7 = Card(rank = 0, suit = 1)
        val combinator = CardCombinator(setOf(card1, card2, card3, card4, card5, card6, card7))

        // when
        val result = combinator.evaluateHand()

        // then
        assertEquals(Combinations.ONE_PAIR, result.first)
        assertEquals(listOf(card2, card3, card7, card6, card1), result.second)
    }

    @Test
    fun `should evaluate high card`() {
        // given
        val card1 = Card(rank = 12, suit = 1)
        val card2 = Card(rank = 1, suit = 0)
        val card3 = Card(rank = 2, suit = 0)
        val card4 = Card(rank = 4, suit = 1)
        val card5 = Card(rank = 5, suit = 0)
        val card6 = Card(rank = 6, suit = 0)
        val card7 = Card(rank = 0, suit = 1)
        val combinator = CardCombinator(setOf(card1, card2, card3, card4, card5, card6, card7))

        // when
        val result = combinator.evaluateHand()

        // then
        assertEquals(Combinations.HIGH_CARD, result.first)
        assertEquals(listOf(card7, card1, card6, card5, card4), result.second)
    }
}