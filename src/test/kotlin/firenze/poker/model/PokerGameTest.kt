package firenze.poker.model

import firenze.poker.enums.Actions
import firenze.poker.enums.Rounds
import firenze.poker.fixture.PokerGameFixture
import io.mockk.every
import io.mockk.mockk
import kotlin.test.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class PokerGameTest {

    lateinit var pokerGame: PokerGame

    @BeforeEach
    fun setUp(){
        pokerGame = PokerGameFixture.pokerGame()
    }

    @Test
    fun `should init poker game when create a poker game object`() {
        val play = PokerGameFixture.plays()
        val pokerGame = PokerGame(players = play)
        assertEquals(0, pokerGame.pot.amounts)
        assertEquals(Rounds.PreFlop, pokerGame.round)
        assertEquals(0, pokerGame.communityCards.size)
        assertEquals(0, pokerGame.buttonPosition)
        assertEquals(1, pokerGame.smallBlindPosition)
        assertEquals(2, pokerGame.bigBlindPosition)

    }

    @Test
    fun `should each play have two cards when button deal cards`() {
        // given
        assertEquals(0, pokerGame.players[0].cards.size)

        // when
        pokerGame.dealCards()

        // then
        assertEquals(2, pokerGame.players[0].cards.size)
    }

    @Test
    fun `should each player take actions in turn when start the round`() {
        // given
        assertEquals(0, pokerGame.pot.amounts)
        assertEquals(emptyList(), pokerGame.hasDoneActionPlays)
        assertEquals(emptyList(), pokerGame.waitingForActionPlayers)

        // when
        pokerGame.startRound()

        // then
        assertEquals(emptyList(), pokerGame.waitingForActionPlayers)
        assertEquals(30, pokerGame.pot.amounts)
    }

    @Test
    fun `should enter next round when this round finish`() {
        // given
        assertEquals(Rounds.PreFlop, pokerGame.round)
        assertEquals(emptyList(), pokerGame.waitingForActionPlayers)

        // when
        pokerGame.startRound()

        // then
        assertEquals(Rounds.Flop, pokerGame.round)
        assertEquals(pokerGame.players, pokerGame.hasDoneActionPlays)
    }

    @Test
    fun `deal community cards when start a round`() {
        // given
        assertEquals(0, pokerGame.communityCards.size)

        // when
        pokerGame.startRound()

        // then
        assertEquals(1, pokerGame.communityCards.size)
    }

    @Test
    fun `should record fold players when there are some players choose to fold`() {
        // given
        val canBet = listOf(Actions.Bet, Actions.Raise, Actions.Check, Actions.Fold)
        val canNotBet = listOf(Actions.Call, Actions.Raise, Actions.Fold)
        val canCheckNotBet = listOf(Actions.Call, Actions.Raise, Actions.Check, Actions.Fold)
        val player1 = mockk<Player>{
            every { takeAction(canBet, 0) } returns Action(Actions.Bet, 10)
            every { takeAction(canNotBet, 20) } returns Action(Actions.Fold, 0)
        }
        val player2 = mockk<Player>{
            every { takeAction(canNotBet, 10) } returns Action(Actions.Raise, 30)
            every { takeAction(canCheckNotBet, 0) } returns Action(Actions.Check, 0)
        }
        val player3 = mockk<Player>{
            every { takeAction(canNotBet, 30) } returns Action(Actions.Call, 30)
            every { takeAction(canCheckNotBet, 0) } returns Action(Actions.Check, 0)
        }
        val pokerGame = PokerGame(listOf(player1, player2, player3))

        // when
        pokerGame.startRound()

        // then
        assertEquals(player1, pokerGame.foldPlayers[0])
        assertEquals(70, pokerGame.pot.amounts)
        assertEquals(listOf(player3, player1, player2), pokerGame.hasDoneActionPlays)
    }

    @Test
    fun `should rearrange waiting list when player took actions`() {
        // given
        val canBet = listOf(Actions.Bet, Actions.Raise, Actions.Check, Actions.Fold)
        val canNotBet = listOf(Actions.Call, Actions.Raise, Actions.Fold)
        val canCheckNotBet = listOf(Actions.Call, Actions.Raise, Actions.Check, Actions.Fold)
        val player1 = mockk<Player>{
            every { takeAction(canBet, 0) } returns Action(Actions.Bet, 10)
            every { takeAction(canNotBet, 20) } returns Action(Actions.Call, 20)
        }
        val player2 = mockk<Player>{
            every { takeAction(canNotBet, 10) } returns Action(Actions.Raise, 30)
            every { takeAction(canCheckNotBet, 0) } returns Action(Actions.Check, 0)
        }
        val player3 = mockk<Player>{
            every { takeAction(canNotBet, 30) } returns Action(Actions.Call, 30)
            every { takeAction(canCheckNotBet, 0) } returns Action(Actions.Check, 0)
        }
        val pokerGame = PokerGame(listOf(player1, player2, player3))

        // when
        pokerGame.startRound()

        // then
        assertEquals(90, pokerGame.pot.amounts)
    }
}