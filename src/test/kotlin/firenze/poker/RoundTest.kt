package firenze.poker

import firenze.poker.domain.AllIn
import firenze.poker.domain.Bet
import firenze.poker.domain.Call
import firenze.poker.domain.Check
import firenze.poker.domain.Fold
import firenze.poker.domain.Player
import firenze.poker.domain.Raise
import firenze.poker.domain.Round
import io.mockk.every
import io.mockk.spyk
import kotlin.test.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class RoundTest {

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
    fun `should load players, set pot and waitingPlayers`() {
        // given && when
        val round = Round(playerA, playerB, playerC)

        // then
        assertEquals(0, round.pot)
        assertEquals(listOf(playerA, playerB, playerC), round.waitingPlayers)
    }

    @Test
    fun `should player B be next player and calculate pot and waiting players when player A bet`() {
        //given
        val round = Round(playerA, playerB, playerC)

        // when
        round.execute(Bet())

        // then
        assertEquals(round.currentBid, round.pot)
        assertEquals("B", round.waitingPlayers.first().name)
    }

    @Test
    fun `should calculate pot and waiting players when player A bet and player B call`() {
        //given
        val round = Round(playerA, playerB, playerC)

        // when
        round.execute(Bet())
        round.execute(Call())

        // then
        assertEquals(round.currentBid * 2, round.pot)
        assertEquals("C", round.waitingPlayers.first().name)
    }

    @Test
    fun `should calculate pot and waiting players when player A fold`() {
        //given
        val round = Round(playerA, playerB, playerC)

        // when
        round.execute(Fold())

        // then
        assertEquals(0, round.pot)
        assertEquals("B", round.waitingPlayers.first().name)
        assertEquals(true, playerB.isActive)
        assertEquals(true, playerC.isActive)
    }

    @Test
    fun `should calculate pot and waiting players when player A check`() {
        //given
        val round = Round(playerA, playerB, playerC)

        // when
        round.execute(Check())

        // then
        assertEquals(0, round.pot)
        assertEquals("B", round.waitingPlayers.first().name)
    }

    @Test
    fun `should calculate pot and waiting players when player A bet and player B raise`() {
        //given
        val mockPlayerC = spyk(Player("C", 10)){
            every { getRaiseWager() } returns 4
        }
        val round = Round(playerA, playerB, mockPlayerC)

        // when
        round.execute(Bet())
        round.execute(Call())
        round.execute(Raise())

        // then
        assertEquals(6, round.pot)
        assertEquals(listOf("A", "B", "C"), round.waitingPlayers.map { it.name })
    }

    @Test
    fun `should enter next round when all players took bids`() {
        //given
        val game = Round(playerA, playerB, playerC)

        // when
        assertEquals("A", game.waitingPlayers.first().name)
        game.execute(Bet())
        assertEquals("B", game.waitingPlayers.first().name)
        game.execute(Call())
        assertEquals("C", game.waitingPlayers.first().name)
        game.execute(Call())

        // then
        assertEquals(3, game.pot)
        assertEquals(true, game.nextRound())
        assertEquals(1, playerA.currentRoundBid)
        assertEquals(1, playerC.currentRoundBid)
        assertEquals(1, playerB.currentRoundBid)
    }

    @Test
    fun `should end next round if all players took fold action`() {
        //given
        val game = Round(playerA, playerB, playerC)

        // when
        assertEquals("A", game.waitingPlayers.first().name)
        game.execute(Fold())
        assertEquals(false, game.nextRound())
        assertEquals("B", game.waitingPlayers.first().name)
        game.execute(Fold())
        assertEquals(false, game.nextRound())
        assertEquals("C", game.waitingPlayers.first().name)
        game.execute(Fold())

        // then
        assertEquals(true, game.nextRound())
    }

    @Test
    fun `should not enter next round when some players didn't take bids`() {
        //given
        val round = Round(playerA, playerB, playerC)

        // when
        assertEquals("A", round.waitingPlayers.first().name)
        round.execute(Bet())
        assertEquals("B", round.waitingPlayers.first().name)
        round.execute(Call())

        // then
        assertEquals(false, round.nextRound())
    }

    @Test
    fun `should make up the difference between current bid and previous bid after someone raise `() {
        //given
        val round = Round(playerA, playerB, playerC)

        // when
        assertEquals("A", round.waitingPlayers.first().name)
        round.execute(Bet())
        val previousBid = round.currentBid
        assertEquals("B", round.waitingPlayers.first().name)
        round.execute(Call())
        assertEquals("C", round.waitingPlayers.first().name)
        round.execute(Raise())
        val previousPotBeforePlayerATakeAction = round.pot
        assertEquals("A", round.waitingPlayers.first().name)
        round.execute(Call())

        // then
        assertEquals(round.currentBid - previousBid, round.pot - previousPotBeforePlayerATakeAction)
    }

    @Test
    fun `should add this player to All-In player list and remove from active player list if a player choose to all-in`() {
        //given
        val mockPlayerB = spyk(Player("B", 10)){
            every { getRaiseWager() } returns 6
        }
        val mockPlayerC = spyk(Player("C", 10)){
            every { getAllInWager() } returns 4
        }
        val round = Round(playerA, mockPlayerB, mockPlayerC)

        // when
        assertEquals("A", round.waitingPlayers.first().name)
        round.execute(Bet())//1
        assertEquals("B", round.waitingPlayers.first().name)
        round.execute(Raise())//6
        assertEquals(7, round.pot)
        assertEquals("C", round.waitingPlayers.first().name)
        round.execute(AllIn())//4
        assertEquals("A", round.waitingPlayers.first().name)
        round.execute(Call())//5

        // then
        assertEquals(16, round.pot)
        assertEquals(true, playerA.isActive)
        assertEquals(true, mockPlayerB.isActive)
        assertEquals(false, mockPlayerC.isActive)
        assertEquals(true, mockPlayerC.isAllIn)
    }
}