package firenze.poker

import firenze.poker.enums.Rounds
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
}