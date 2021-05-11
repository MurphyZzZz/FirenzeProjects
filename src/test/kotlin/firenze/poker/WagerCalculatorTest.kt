package firenze.poker

import firenze.poker.domain.Game
import firenze.poker.domain.Player
import firenze.poker.enums.Rounds
import firenze.poker.utils.WagerCalculator
import io.mockk.spyk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class WagerCalculatorTest {

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
    fun `should give all wager to player when only one winner`() {
        //given
        val game = spyk(Game(playerA, playerB, playerC))

        // when
        playerA.totalBid = 1
        playerB.totalBid = 1
        playerC.totalBid = 1
        game.totalWager = 3
        WagerCalculator.splitWager(game, listOf(setOf(playerA)))

        // then
        kotlin.test.assertEquals(12, playerA.money)
    }

    @Test
    fun `should split wager when more one winner with same cards`() {
        //given
        val game = spyk(Game(playerA, playerB, playerC))

        // when
        playerA.totalBid = 4
        playerB.totalBid = 4
        playerC.totalBid = 4
        game.totalWager = 12
        WagerCalculator.splitWager(game, listOf(setOf(playerA, playerB)))

        // then
        kotlin.test.assertEquals(12, playerA.money)
        kotlin.test.assertEquals(12, playerB.money)
    }

    @Test
    fun `should split wager when first winner set has one winner and second set has two winners`() {
        //given
        playerA.money = 20
        playerB.money = 20
        val game = spyk(Game(playerA, playerB, playerC))
        game.currentRoundName = Rounds.TURN
        game.totalWager = 12
        val round = game.round

        // when
        playerA.totalBid = 10
        playerB.totalBid = 10
        playerC.totalBid = 10
        game.totalWager = 30
        WagerCalculator.splitWager(game, listOf(setOf(playerC), setOf(playerA, playerB)))

        // then
        kotlin.test.assertEquals(30, playerC.money)
        kotlin.test.assertEquals(10, playerA.money)
        kotlin.test.assertEquals(10, playerB.money)
    }

    @Test
    fun `should split wager when first winner set has more than one winner and second set has one winner`() {
        //given
        val game = spyk(Game(playerA, playerB, playerC))

        // when
        playerA.totalBid = 3
        playerB.totalBid = 3
        playerC.totalBid = 8
        game.totalWager = 14
        WagerCalculator.splitWager(game, listOf(setOf(playerA, playerB), setOf(playerC)))

        // then
        kotlin.test.assertEquals(7, playerC.money)
        kotlin.test.assertEquals(11, playerA.money)
        kotlin.test.assertEquals(11, playerB.money)
    }
}