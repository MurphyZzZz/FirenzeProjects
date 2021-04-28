package firenze.poker.model

import io.mockk.every
import io.mockk.spyk
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

internal class PotTest{

    @Test
    fun `should evenly distribute amounts to winners when more than one winner exist`() {
        // given
        val pot = spyk<Pot>()
        pot.amounts = 100
        val player1 = Player(name = "Lisa", amounts = 100)
        val player2 = Player(name = "Kate", amounts = 100)
        val players = listOf(player1, player2)
        pot.potentialWinners.addAll(players)
        every {
            pot["compareCards"](players)
        } returns players

        // when
        pot.distributeAmounts()

        // then
        assertEquals(150, player1.amounts)
        assertEquals(150, player2.amounts)
    }

    @Test
    fun `should evenly distribute all amounts one winner when there is only one winner exists`() {
        // given
        val pot = spyk<Pot>()
        pot.amounts = 100
        val player1 = Player(name = "Lisa", amounts = 100)
        val player2 = Player(name = "Kate", amounts = 100)
        val players = listOf(player1, player2)
        pot.potentialWinners.addAll(players)
        every {
            pot["compareCards"](players)
        } returns listOf(player1)

        // when
        pot.distributeAmounts()

        // then
        assertEquals(200, player1.amounts)
        assertEquals(100, player2.amounts)
    }
}