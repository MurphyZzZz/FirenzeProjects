package firenze.poker.service

import firenze.poker.exception.InvalidNumberOfPlays
import firenze.poker.fixture.PokerGameFixture
import firenze.poker.model.Player
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class PokerGameServiceTest {

    @InjectMockKs
    private lateinit var service: PokerGameService

    @Test
    fun `should init game config when start the game given valid plays number`() {
        // given
        val pokerGame = PokerGameFixture.pokerGame()

        // when
        val result = service.start(pokerGame.players)

        // then
        assertEquals(pokerGame.players, result.players)
    }

    @Test
    fun `should throw exception when start the game given invalid plays number which is bigger than 10`() {
        // given
        val plays = mockk<List<Player>>{
            every { size } returns 11
        }

        // when && then
        assertThrows<InvalidNumberOfPlays> { service.start(plays) }
    }

    @Test
    fun `should throw exception when start the game given invalid plays number which is smaller than 2`() {
        // given
        val plays = mockk<List<Player>>{
            every { size } returns 1
        }

        // when && then
        assertThrows<InvalidNumberOfPlays> { service.start(plays) }
    }

}