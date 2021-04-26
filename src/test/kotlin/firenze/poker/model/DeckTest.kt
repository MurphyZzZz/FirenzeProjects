package firenze.poker.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DeckTest {

    @Test
    fun `should init cards when create a deck object`() {
        // when && then
        assertEquals(52, Deck().initCards().size)
    }
}