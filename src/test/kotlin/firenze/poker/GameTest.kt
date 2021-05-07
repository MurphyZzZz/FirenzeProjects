package firenze.poker

import Game
import firenze.poker.enums.Round
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

internal class GameTest {

    @Test
    fun `should load players, set pot and waitingPlayers`() {
        // given && when
        val game = Game(Player("A"), Player("B"), Player("C"))

        // then
        assertEquals(0, game.pot)
        assertEquals(listOf(Player("A"), Player("B"), Player("C")), game.waitingPlayers)
    }

    @Test
    fun `should player B be next player and calculate pot and waiting players when player A bet`() {
        //given
        val game = Game(Player("A"), Player("B"), Player("C"))

        // when
        game.bet()

        // then
        assertEquals(game.currentBid, game.pot)
        assertEquals("B", game.waitingPlayers.first().name)
    }

    @Test
    fun `should calculate pot and waiting players when player A bet and player B call`() {
        //given
        val game = Game(Player("A"), Player("B"), Player("C"))

        // when
        game.bet()
        game.call()

        // then
        assertEquals(game.currentBid * 2, game.pot)
        assertEquals("C", game.waitingPlayers.first().name)
    }

    @Test
    fun `should calculate pot and waiting players when player A fold`() {
        //given
        val game = Game(Player("A"), Player("B"), Player("C"))

        // when
        game.fold()

        // then
        assertEquals(0, game.pot)
        assertEquals("B", game.waitingPlayers.first().name)
        assertEquals(listOf("B", "C"), game.activePlayers.map { it.name })
    }

    @Test
    fun `should calculate pot and waiting players when player A check`() {
        //given
        val game = Game(Player("A"), Player("B"), Player("C"))

        // when
        game.check()

        // then
        assertEquals(0, game.pot)
        assertEquals("B", game.waitingPlayers.first().name)
    }

    @Test
    fun `should calculate pot and waiting players when player A bet and player B raise`() {
        //given
        val game = Game(Player("A"), Player("B"), Player("C"))

        // when
        game.bet()
        game.call()
        game.raise(4)

        // then
        assertEquals(6, game.pot)
        assertEquals(listOf("A", "B", "C"), game.waitingPlayers.map { it.name })
        assertEquals(Round.PREFLOP, game.round)
    }

    @Test
    fun `should enter next round when all players took bids`() {
        //given
        val game = Game(Player("A"), Player("B"), Player("C"))

        // when
        assertEquals("A", game.waitingPlayers.first().name)
        game.bet()
        assertEquals("B", game.waitingPlayers.first().name)
        game.call()
        assertEquals("C", game.waitingPlayers.first().name)
        game.call()

        // then
        assertEquals(3, game.pot)
        assertEquals(Round.FLOP, game.round)
        assertEquals(1, game.hasDonePlayersAndWager[Player("A")])
        assertEquals(1, game.hasDonePlayersAndWager[Player("B")])
        assertEquals(1, game.hasDonePlayersAndWager[Player("C")])
    }

    @Test
    fun `should not enter next round when some players didn't take bids`() {
        //given
        val game = Game(Player("A"), Player("B"), Player("C"))

        // when
        assertEquals("A", game.waitingPlayers.first().name)
        game.bet()
        assertEquals("B", game.waitingPlayers.first().name)
        game.call()

        // then
        assertEquals(Round.PREFLOP, game.round)
    }

    @Test
    fun `should make up the difference between current bid and privous bid after someone raise `() {
        //given
        val game = Game(Player("A"), Player("B"), Player("C"))

        // when
        assertEquals("A", game.waitingPlayers.first().name)
        game.bet()
        val previousBid = game.currentBid
        assertEquals("B", game.waitingPlayers.first().name)
        game.call()
        assertEquals("C", game.waitingPlayers.first().name)
        game.raise(4)
        val previousPotBeforePlayerATakeAction = game.pot
        assertEquals("A", game.waitingPlayers.first().name)
        game.call()

        // then
        assertEquals(game.currentBid - previousBid, game.pot - previousPotBeforePlayerATakeAction)
    }
}