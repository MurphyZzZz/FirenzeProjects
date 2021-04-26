package firenze.poker.model

import firenze.poker.enums.Actions
import firenze.poker.enums.Rounds

class PokerGame(
    val plays: List<Play>
) {
    val round = Rounds.PreFlop
    val pot = Pot(amounts = 0)
    val communityCards: List<Card> = mutableListOf<Card>()
    private val deck = Deck()
    val buttonPosition = 0
    val smallBlindPosition = 1
    val bigBlindPosition = 2

    lateinit var waitingForActionPlays: MutableList<Play>
    val hasDoneActionPlays = mutableListOf<Play>()
    val availableActions = mutableListOf<Actions>()
    lateinit var currentActionPlay: Play
    var currentMaximumBetAmounts = 0

    fun dealCards() {
        plays.forEach {
            it.setCard(deck.getCard())
            it.setCard(deck.getCard())
        }
    }

    fun startRound() {
        waitingForActionPlays = initWaitingActionList()

        while (waitingForActionPlays.firstOrNull() != null){
            currentActionPlay = waitingForActionPlays.removeFirst()

            val currentActionAmounts = currentActionPlay.takeAction(availableActions, currentMaximumBetAmounts).amounts
            pot.amounts += currentActionAmounts
            currentMaximumBetAmounts = currentActionAmounts.coerceAtLeast(currentMaximumBetAmounts)

            hasDoneActionPlays.add(currentActionPlay)
        }
    }

    private fun initWaitingActionList(): MutableList<Play> {
        return if (round == Rounds.PreFlop) {
            (plays.subList(bigBlindPosition + 1, plays.size) + plays.subList(0, bigBlindPosition + 1)).toMutableList()
        } else {
            (plays.subList(buttonPosition + 1, plays.size) + plays[buttonPosition]).toMutableList()
        }
    }
}