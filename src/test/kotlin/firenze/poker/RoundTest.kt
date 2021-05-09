package firenze.poker

import Round
import firenze.poker.enums.Rounds
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
        val game = Round(playerA, playerB, playerC)

        // then
        assertEquals(0, game.pot)
        assertEquals(listOf(playerA, playerB, playerC), game.waitingPlayers)
    }

    @Test
    fun `should player B be next player and calculate pot and waiting players when player A bet`() {
        //given
        val game = Round(playerA, playerB, playerC)

        // when
        game.execute(Bet())

        // then
        assertEquals(game.currentBid, game.pot)
        assertEquals("B", game.waitingPlayers.first().name)
    }

    @Test
    fun `should calculate pot and waiting players when player A bet and player B call`() {
        //given
        val game = Round(playerA, playerB, playerC)

        // when
        game.execute(Bet())
        game.execute(Call())

        // then
        assertEquals(game.currentBid * 2, game.pot)
        assertEquals("C", game.waitingPlayers.first().name)
    }

    @Test
    fun `should calculate pot and waiting players when player A fold`() {
        //given
        val game = Round(playerA, playerB, playerC)

        // when
        game.execute(Fold())

        // then
        assertEquals(0, game.pot)
        assertEquals("B", game.waitingPlayers.first().name)
        assertEquals(true, playerB.isActive)
        assertEquals(true, playerC.isActive)
    }

    @Test
    fun `should calculate pot and waiting players when player A check`() {
        //given
        val game = Round(playerA, playerB, playerC)

        // when
        game.execute(Check())

        // then
        assertEquals(0, game.pot)
        assertEquals("B", game.waitingPlayers.first().name)
    }

    @Test
    fun `should calculate pot and waiting players when player A bet and player B raise`() {
        //given
        val mockPlayerC = spyk(Player("C", 10)){
            every { getRaiseWager() } returns 4
        }
        val game = Round(playerA, playerB, mockPlayerC)

        // when
        game.execute(Bet())
        game.execute(Call())
        game.execute(Raise())

        // then
        assertEquals(6, game.pot)
        assertEquals(listOf("A", "B", "C"), game.waitingPlayers.map { it.name })
        assertEquals(Rounds.PREFLOP, game.round)
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
        assertEquals(Rounds.FLOP, game.round)
        assertEquals(1, playerA.currentRoundBid)
        assertEquals(1, playerC.currentRoundBid)
        assertEquals(1, playerB.currentRoundBid)
    }

    @Test
    fun `should not enter next round when some players didn't take bids`() {
        //given
        val game = Round(playerA, playerB, playerC)

        // when
        assertEquals("A", game.waitingPlayers.first().name)
        game.execute(Bet())
        assertEquals("B", game.waitingPlayers.first().name)
        game.execute(Call())

        // then
        assertEquals(Rounds.PREFLOP, game.round)
    }

    @Test
    fun `should make up the difference between current bid and previous bid after someone raise `() {
        //given
        val game = Round(playerA, playerB, playerC)

        // when
        assertEquals("A", game.waitingPlayers.first().name)
        game.execute(Bet())
        val previousBid = game.currentBid
        assertEquals("B", game.waitingPlayers.first().name)
        game.execute(Call())
        assertEquals("C", game.waitingPlayers.first().name)
        game.execute(Raise())
        val previousPotBeforePlayerATakeAction = game.pot
        assertEquals("A", game.waitingPlayers.first().name)
        game.execute(Call())

        // then
        assertEquals(game.currentBid - previousBid, game.pot - previousPotBeforePlayerATakeAction)
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
        val game = Round(playerA, mockPlayerB, mockPlayerC)

        // when
        assertEquals("A", game.waitingPlayers.first().name)
        game.execute(Bet())//1
        assertEquals("B", game.waitingPlayers.first().name)
        game.execute(Raise())//6
        assertEquals(7, game.pot)
        assertEquals("C", game.waitingPlayers.first().name)
        game.execute(AllIn())//4
        assertEquals("A", game.waitingPlayers.first().name)
        game.execute(Call())//5

        // then
        assertEquals(16, game.pot)
        assertEquals(true, playerA.isActive)
        assertEquals(true, mockPlayerB.isActive)
        assertEquals(false, mockPlayerC.isActive)
        assertEquals(true, mockPlayerC.isAllIn)
    }
}