package firenze.poker

import firenze.poker.domain.AllIn
import firenze.poker.domain.Bet
import firenze.poker.domain.Call
import firenze.poker.domain.Fold
import firenze.poker.domain.Game
import firenze.poker.domain.Player
import firenze.poker.enums.Rounds
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import kotlin.test.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class GameTest {

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
    fun `should enter next round if all players have token actions`() {
        //given
        val game = Game(playerA, playerB, playerC)
        val round = game.round

        // when
        assertEquals("A", round.waitingPlayers.first().name)
        game.execute(Bet())
        assertEquals("B", round.waitingPlayers.first().name)
        game.execute(Call())
        assertEquals("C", round.waitingPlayers.first().name)
        game.execute(Call())

        // then
        assertEquals(Rounds.FLOP, game.currentRoundName)
    }

    @Test
    fun `should prepare next round configuration when this round finished`() {
        //given
        val game = Game(playerA, playerB, playerC)
        game.currentRoundName = Rounds.PREFLOP
        val round = game.round

        // when
        assertEquals("A", round.waitingPlayers.first().name)
        game.execute(Bet())
        assertEquals("B", round.waitingPlayers.first().name)
        game.execute(Call())
        assertEquals("C", round.waitingPlayers.first().name)
        game.execute(Fold())

        // then
        assertEquals(Rounds.FLOP, game.currentRoundName)
        assertEquals(0, game.round.pot)
        assertEquals(0, game.round.currentBid)
        assertEquals(listOf("A", "B"), game.round.waitingPlayers.map { it.name })
    }

    @Test
    fun `should end of the game and shut down if all rounds finished`() {
        //given
        val game = Game(playerA, playerB, playerC)
        game.currentRoundName = Rounds.RIVER
        val round = game.round

        // when
        assertEquals("A", round.waitingPlayers.first().name)
        game.execute(Bet())
        assertEquals("B", round.waitingPlayers.first().name)
        game.execute(Call())
        assertEquals("C", round.waitingPlayers.first().name)
        game.execute(Call())

        // then
        assertEquals(true, game.end)
    }

    @Test
    fun `should end game if only one player left`() {
        //given
        val game = Game(playerA, playerB, playerC)
        game.currentRoundName = Rounds.TURN
        val round = game.round

        // when
        assertEquals("A", round.waitingPlayers.first().name)
        game.execute(Bet())
        assertEquals("B", round.waitingPlayers.first().name)
        game.execute(Fold())
        assertEquals("C", round.waitingPlayers.first().name)
        game.execute(Fold())

        // then
        assertEquals(true, game.end)
    }

    @Test
    fun `should give all wager to player when only one winner`() {
        //given
        val game = spyk(Game(playerA, playerB, playerC))
        game.currentRoundName = Rounds.RIVER
        val round = game.round

        every {
            game["shutDown"]()
        } returns listOf(setOf(playerA))

        // when
        assertEquals("A", round.waitingPlayers.first().name)
        game.execute(Bet())

        assertEquals(1, playerA.totalBid)
        assertEquals(10, playerA.money)

        assertEquals("B", round.waitingPlayers.first().name)
        game.execute(Call())
        assertEquals("C", round.waitingPlayers.first().name)
        game.execute(Call())

        // then
        assertEquals(true, game.end)
        assertEquals(3, game.totalWager)
        assertEquals(12, playerA.money)
    }

    @Test
    fun `should split wager when more one winner with same cards`() {
        //given
        playerA.totalBid = 3
        playerB.totalBid = 3
        val game = spyk(Game(playerA, playerB, playerC))
        game.currentRoundName = Rounds.RIVER
        game.totalWager = 9
        val round = game.round

        every {
            game["shutDown"]()
        } returns listOf(setOf(playerA, playerB))

        // when
        assertEquals("A", round.waitingPlayers.first().name)
        game.execute(Bet())

        assertEquals(4, playerA.totalBid)
        assertEquals(10, playerA.money)

        assertEquals("B", round.waitingPlayers.first().name)
        game.execute(Call())

        assertEquals(4, playerB.totalBid)
        assertEquals(10, playerB.money)

        assertEquals("C", round.waitingPlayers.first().name)
        game.execute(Call())

        // then
        assertEquals(true, game.end)
        assertEquals(0, game.totalWager)
        assertEquals(12, playerA.money)
        assertEquals(12, playerB.money)
    }

    @Test
    fun `should split wager when first winner set has one winner and second set has two winners`() {
        //given
        playerA.money = 20
        playerB.money = 20
        playerA.totalBid = 4
        playerB.totalBid = 4
        playerC.totalBid = 4
        val game = spyk(Game(playerA, playerB, playerC))
        game.currentRoundName = Rounds.TURN
        game.totalWager = 12
        val round = game.round

        every {
            game["shutDown"]()
        } returns listOf(setOf(playerC), setOf(playerA, playerB))

        // when
        assertEquals("A", round.waitingPlayers.first().name)
        game.execute(Bet())//1

        assertEquals(5, playerA.totalBid)
        assertEquals(20, playerA.money)

        assertEquals("B", round.waitingPlayers.first().name)
        game.execute(Call())//1

        assertEquals(5, playerB.totalBid)
        assertEquals(20, playerB.money)

        assertEquals("C", round.waitingPlayers.first().name)
        game.execute(AllIn())//6

        assertEquals(10, playerC.totalBid)
        assertEquals(10, playerC.money)

        assertEquals("A", round.waitingPlayers.first().name)
        game.execute(Call())//5

        assertEquals(10, playerA.totalBid)

        assertEquals("B", round.waitingPlayers.first().name)
        game.execute(Call())//5

        assertEquals(10, playerB.totalBid)

        assertEquals(Rounds.RIVER, game.currentRoundName)
        assertEquals(listOf("A", "B"), round.waitingPlayers.map { it.name })

        assertEquals("A", round.waitingPlayers.first().name)
        game.execute(Bet())//1

        assertEquals(1, playerA.currentRoundBid)
        assertEquals(11, playerA.totalBid)

//        assertEquals("B", round.waitingPlayers.first().name)
        game.execute(Call())//1

        assertEquals(1, playerB.currentRoundBid)
        assertEquals(11, playerB.totalBid)

        // then
        assertEquals(true, game.end)
        assertEquals(30, playerC.money)
        assertEquals(10, playerA.money)
        assertEquals(10, playerB.money)
    }

    @Test
    fun `should split wager when first winner set has more than one winner and second set has one winner`() {
        //given
        playerA.totalBid = 2
        playerB.totalBid = 2
        playerC.totalBid = 7
        val game = spyk(Game(playerA, playerB, playerC))
        game.currentRoundName = Rounds.RIVER
        game.totalWager = 11
        val round = game.round

        every {
            game["shutDown"]()
        } returns listOf(setOf(playerA, playerB), setOf(playerC))

        // when
        assertEquals("A", round.waitingPlayers.first().name)
        game.execute(Bet())

        assertEquals(3, playerA.totalBid)
        assertEquals(10, playerA.money)

        assertEquals("B", round.waitingPlayers.first().name)
        game.execute(Call())

        assertEquals(3, playerB.totalBid)
        assertEquals(10, playerB.money)

        assertEquals("C", round.waitingPlayers.first().name)
        game.execute(Call())

        assertEquals(8, playerC.totalBid)

        // then
        assertEquals(true, game.end)
        assertEquals(7, playerC.money)
        assertEquals(11, playerA.money)
        assertEquals(11, playerB.money)
    }

    @Test
    fun `should deal two cards for each player before pre-flop`() {
        // given
        val game = Game(playerA, playerB, playerC)

        // when
        assertEquals(0, playerA.cards.size)
        assertEquals(0, playerB.cards.size)
        assertEquals(0, playerC.cards.size)
        game.dealCards()

        // then
        assertEquals(2, playerA.cards.size)
        assertEquals(2, playerB.cards.size)
        assertEquals(2, playerC.cards.size)
    }

    @Test
    fun `should deal three community cards at flop round`() {
        // given
        val game = Game(playerA, playerB, playerC)

        // when
        assertEquals(Rounds.PREFLOP, game.currentRoundName)
        assertEquals(0, game.communityCards.size)

        game.execute(Bet())
        game.execute(Call())
        game.execute(Call())

        assertEquals(Rounds.FLOP, game.currentRoundName)

        // then
        assertEquals(3, game.communityCards.size)
    }

    @Test
    fun `should deal one community cards in post-flop round`() {
        // given
        val game = Game(playerA, playerB, playerC)
        game.currentRoundName = Rounds.FLOP
        game.communityCards.addAll(listOf(mockk(), mockk(), mockk()))

        // when
        assertEquals(Rounds.FLOP, game.currentRoundName)
        assertEquals(3, game.communityCards.size)

        game.execute(Bet())
        game.execute(Call())
        game.execute(Call())

        assertEquals(Rounds.TURN, game.currentRoundName)

        // then
        assertEquals(4, game.communityCards.size)
    }
}