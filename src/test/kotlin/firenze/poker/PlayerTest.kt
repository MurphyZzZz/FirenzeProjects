package firenze.poker

import firenze.poker.domain.Player
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

internal class PlayerTest {

    @Test
    fun `should recalculate current round bid and total bid when player took actions`() {
        // given
        val player = Player("A", 10)

        // when && then
        player.calculateMoney(3)
        assertEquals(3, player.currentRoundBid)
        assertEquals(3, player.totalBid)
        player.calculateMoneyForCall(5)
        assertEquals(5, player.currentRoundBid)
        assertEquals(5, player.totalBid)
    }
}