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
}