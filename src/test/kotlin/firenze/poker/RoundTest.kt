package firenze.poker

import Round
import firenze.poker.enums.Rounds
import io.mockk.every
import io.mockk.mockk
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

internal class RoundTest {

    @Test
    fun `should load players, set pot and waitingPlayers`() {
        // given && when
        val game = Round(Player("A"), Player("B"), Player("C"))

        // then
        assertEquals(0, game.pot)
        assertEquals(listOf(Player("A"), Player("B"), Player("C")), game.waitingPlayers)
    }

    @Test
    fun `should player B be next player and calculate pot and waiting players when player A bet`() {
        //given
        val game = Round(Player("A"), Player("B"), Player("C"))

        // when
        game.execute(Bet())

        // then
        assertEquals(game.currentBid, game.pot)
        assertEquals("B", game.waitingPlayers.first().name)
    }

    @Test
    fun `should calculate pot and waiting players when player A bet and player B call`() {
        //given
        val game = Round(Player("A"), Player("B"), Player("C"))

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
        val game = Round(Player("A"), Player("B"), Player("C"))

        // when
        game.execute(Fold())

        // then
        assertEquals(0, game.pot)
        assertEquals("B", game.waitingPlayers.first().name)
        assertEquals(listOf("B", "C"), game.activePlayers.map { it.name })
    }

    @Test
    fun `should calculate pot and waiting players when player A check`() {
        //given
        val game = Round(Player("A"), Player("B"), Player("C"))

        // when
        game.execute(Check())

        // then
        assertEquals(0, game.pot)
        assertEquals("B", game.waitingPlayers.first().name)
    }

    @Test
    fun `should calculate pot and waiting players when player A bet and player B raise`() {
        //given
        val mockPlayerC = mockk<Player>{
            every { name } returns "C"
            every { getRaiseWager() } returns 4
        }
        val game = Round(Player("A"), Player("B"), mockPlayerC)

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
        val game = Round(Player("A"), Player("B"), Player("C"))

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
        assertEquals(1, game.hasDonePlayersAndWager[Player("A")])
        assertEquals(1, game.hasDonePlayersAndWager[Player("B")])
        assertEquals(1, game.hasDonePlayersAndWager[Player("C")])
    }

    @Test
    fun `should not enter next round when some players didn't take bids`() {
        //given
        val game = Round(Player("A"), Player("B"), Player("C"))

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
        val game = Round(Player("A"), Player("B"), Player("C"))

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
        val mockPlayerB = mockk<Player>{
            every { name } returns "B"
            every { getRaiseWager() } returns 6
        }
        val mockPlayerC = mockk<Player>{
            every { name } returns "C"
            every { getAllInWager() } returns 4
        }
        val game = Round(Player("A"), mockPlayerB, mockPlayerC)

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
        assertEquals(listOf("A", "B"), game.activePlayers.map { it.name })
        assertEquals(listOf("C"), game.allInPlayers.map { it.name })
    }
}