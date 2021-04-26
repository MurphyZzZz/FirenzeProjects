package firenze.poker.model

import firenze.poker.enums.Actions
import firenze.poker.enums.Rounds

class PokerGame(
    val players: List<Player>
) {
    var round = Rounds.PreFlop
    val pot = Pot(amounts = 0)
    val communityCards: List<Card> = mutableListOf<Card>()
    private val deck = Deck()
    val buttonPosition = 0
    val smallBlindPosition = 1
    val bigBlindPosition = 2

    lateinit var waitingForActionPlayers: MutableList<Player>
    val hasDoneActionPlays = mutableListOf<Player>()
    val availableActions = mutableListOf<Actions>()
    lateinit var currentActionPlayer: Player
    var currentMaximumBetAmounts = 0

    fun dealCards() {
        players.forEach {
            it.setCard(deck.getCard())
            it.setCard(deck.getCard())
        }
    }

    fun startRound() {
        waitingForActionPlayers = initWaitingActionList()

        playerTakeActionInTurn()

        prepareForNextRound()
    }

    private fun playerTakeActionInTurn() {
        while (waitingForActionPlayers.firstOrNull() != null) {
            currentActionPlayer = waitingForActionPlayers.removeFirst()

            val currentActionAmounts = currentActionPlayer.takeAction(availableActions, currentMaximumBetAmounts).amounts
            pot.amounts += currentActionAmounts
            currentMaximumBetAmounts = currentActionAmounts.coerceAtLeast(currentMaximumBetAmounts)

            hasDoneActionPlays.add(currentActionPlayer)
        }
    }

    private fun prepareForNextRound() {
        Rounds.getNextRound(round)?.let {
            round = it
            hasDoneActionPlays.clear()
            currentMaximumBetAmounts = 0
        }
    }

    private fun initWaitingActionList(): MutableList<Player> {
        return if (round == Rounds.PreFlop) {
            (players.subList(bigBlindPosition + 1, players.size) + players.subList(0, bigBlindPosition + 1)).toMutableList()
        } else {
            (players.subList(buttonPosition + 1, players.size) + players[buttonPosition]).toMutableList()
        }
    }
}