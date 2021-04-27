package firenze.poker.model

import firenze.poker.enums.Actions
import firenze.poker.enums.Rounds
import firenze.poker.fixture.PokerGameFixture
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import kotlin.test.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class PokerGameTest {

    private lateinit var pokerGame: PokerGame

    @BeforeEach
    fun setUp(){
        pokerGame = PokerGameFixture.pokerGame()
    }

    @Test
    fun `should init poker game when create a poker game object`() {
        val play = PokerGameFixture.plays()
        val pokerGame = PokerGame(players = play)
        assertEquals(0, pokerGame.pots.size)
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
        assertEquals(0, pokerGame.pots.size)
        assertEquals(emptyList(), pokerGame.hasDoneActionPlays)
        assertEquals(emptyList(), pokerGame.waitingForActionPlayers)

        // when
        pokerGame.startRound()

        // then
        assertEquals(emptyList(), pokerGame.waitingForActionPlayers)
        assertEquals(30, pokerGame.pots.last().amounts)
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
    fun `deal community cards when start pre-flop round`() {
        // given
        assertEquals(0, pokerGame.communityCards.size)

        // when
        pokerGame.startRound()

        // then
        assertEquals(0, pokerGame.communityCards.size)
    }

    @Test
    fun `deal community cards when start a flop round`() {
        // given
        val pokerGame1 = spyk(PokerGame(pokerGame.players))
        pokerGame1.round = Rounds.Flop

        every {
            pokerGame1["initWaitingActionList"]()
        } returns ""

        every {
            pokerGame1["playerTakeActionInTurn"]()
        } returns ""

        every {
            pokerGame1["prepareForNextRound"]()
        } returns ""

        // when
        pokerGame1.startRound()

        // then
        assertEquals(3, pokerGame1.communityCards.size)
    }

    @Test
    fun `deal community cards when start other round except pre-flop and flop`() {
        // given
        val pokerGame1 = spyk(PokerGame(pokerGame.players))
        pokerGame1.round = Rounds.River

        every {
            pokerGame1["initWaitingActionList"]()
        } returns ""

        every {
            pokerGame1["playerTakeActionInTurn"]()
        } returns ""

        every {
            pokerGame1["prepareForNextRound"]()
        } returns ""

        // when
        pokerGame1.startRound()

        // then
        assertEquals(1, pokerGame1.communityCards.size)
    }

    @Test
    fun `should record fold players when there are some players choose to fold`() {
        // given
        val canBet = listOf(Actions.Bet, Actions.Raise, Actions.Check, Actions.Fold)
        val canNotBet = listOf(Actions.Call, Actions.Raise, Actions.Fold)
        val canCheckNotBet = listOf(Actions.Call, Actions.Raise, Actions.Check, Actions.Fold)
        val player1 = mockk<Player>{
            every { name } returns "player1"
            every { takeAction(canBet, 0) } returns Action(Actions.Bet, 10)
            every { takeAction(canNotBet, 20) } returns Action(Actions.Fold, 0)
        }
        val player2 = mockk<Player>{
            every { name } returns "player2"
            every { takeAction(canNotBet, 10) } returns Action(Actions.Raise, 30)
            every { takeAction(canCheckNotBet, 0) } returns Action(Actions.Check, 0)
        }
        val player3 = mockk<Player>{
            every { name } returns "player3"
            every { takeAction(canNotBet, 30) } returns Action(Actions.Call, 30)
            every { takeAction(canCheckNotBet, 0) } returns Action(Actions.Check, 0)
        }
        val pokerGame = PokerGame(listOf(player1, player2, player3))

        // when
        pokerGame.startRound()

        // then
        assertEquals(player1, pokerGame.foldPlayers[0])
        assertEquals(70, pokerGame.pots.last().amounts)
        assertEquals(listOf(player2, player3, player1), pokerGame.hasDoneActionPlays)
    }

    @Test
    fun `should rearrange waiting list when player took actions`() {
        // given
        val canBet = listOf(Actions.Bet, Actions.Raise, Actions.Check, Actions.Fold)
        val canNotBet = listOf(Actions.Call, Actions.Raise, Actions.Fold)
        val canCheckNotBet = listOf(Actions.Call, Actions.Raise, Actions.Check, Actions.Fold)
        val player1 = mockk<Player>{
            every { name } returns "player1"
            every { takeAction(canBet, 0) } returns Action(Actions.Bet, 10)
            every { takeAction(canNotBet, 20) } returns Action(Actions.Call, 20)
        }
        val player2 = mockk<Player>{
            every { name } returns "player2"
            every { takeAction(canNotBet, 10) } returns Action(Actions.Raise, 30)
            every { takeAction(canCheckNotBet, 0) } returns Action(Actions.Check, 0)
        }
        val player3 = mockk<Player>{
            every { name } returns "player3"
            every { takeAction(canNotBet, 30) } returns Action(Actions.Call, 30)
            every { takeAction(canCheckNotBet, 0) } returns Action(Actions.Check, 0)
        }
        val pokerGame = PokerGame(listOf(player1, player2, player3))

        // when
        pokerGame.startRound()

        // then
        assertEquals(90, pokerGame.pots.last().amounts)
    }

    @Test
    fun `should split pot when player choose to all-in`() {
        // given
        val canBet = listOf(Actions.Bet, Actions.Raise, Actions.Check, Actions.Fold)
        val canNotBet = listOf(Actions.Call, Actions.Raise, Actions.Fold)
        val canCheckNotBet = listOf(Actions.Call, Actions.Raise, Actions.Check, Actions.Fold)
        val player1 = mockk<Player>{
            every { name } returns "player1"
            every { takeAction(canBet, 0) } returns Action(Actions.Bet, 10)
            every { takeAction(canNotBet, 10) } returns Action(Actions.Call, 10)
        }
        val player2 = mockk<Player>{
            every { name } returns "player2"
            every { takeAction(canNotBet, 10) } returns Action(Actions.Call, 10)
            every { takeAction(canCheckNotBet, 10) } returns Action(Actions.Raise, 20)
        }
        val player3 = mockk<Player>{
            every { name } returns "player3"
            every { takeAction(canNotBet, 10) } returns Action(Actions.AllIn, 20)
        }
        val pokerGame = PokerGame(listOf(player1, player2, player3))

        // when
        pokerGame.startRound()

        // then
        assertEquals(listOf(player1, player2), pokerGame.hasDoneActionPlays)
        assertEquals(20, pokerGame.pots.last().amounts)
        assertEquals(listOf(player1, player2), pokerGame.pots.last().potentialWinners)
        assertEquals(40, pokerGame.pots[0].amounts)
        assertEquals(listOf(player3, player1, player2), pokerGame.pots[0].potentialWinners)
    }
}