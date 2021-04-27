package firenze.poker.model

import firenze.poker.enums.Actions
import firenze.poker.enums.Rounds

class PokerGame(
    val players: List<Player>
) {
    var round = Rounds.PreFlop
    val pot = Pot(amounts = 0)
    val communityCards: MutableList<Card> = mutableListOf()
    private val deck = Deck()
    val buttonPosition = 0
    val smallBlindPosition = 1
    val bigBlindPosition = 2

    var waitingForActionPlayers = mutableListOf<Player>()
    val hasDoneActionPlays = mutableListOf<Player>()
    val foldPlayers = mutableListOf<Player>()
    var currentMaximumBetAmounts = 0
    private val alreadyBetAmountsMap = mutableMapOf<Player, Int>()


    fun dealCards() {
        players.forEach {
            it.setCard(deck.getCard())
            it.setCard(deck.getCard())
        }
    }

    private fun dealCommunityCard() {
        val newCard = deck.getCard()
        newCard.showCard()
        communityCards.add(newCard)
    }

    fun startRound() {

        dealCommunityCard()

        initWaitingActionList()

        playerTakeActionInTurn()

        prepareForNextRound()
    }

    private fun calculateAvailableActions(alreadyBet: Int, currentMaximumBetAmounts: Int): List<Actions> {
        if (currentMaximumBetAmounts == 0){
            return listOf(Actions.Bet, Actions.Raise, Actions.Check, Actions.Fold)
        }else if (alreadyBet < currentMaximumBetAmounts){
            return listOf(Actions.Call, Actions.Raise, Actions.Fold)
        }else{
            return listOf(Actions.Call, Actions.Raise, Actions.Check, Actions.Fold)
        }
    }

    private fun rearrangeWaitingList(action: Actions, currentActionPlayer: Player, waitingForActionPlayers: MutableList<Player>, hasDoneActionPlays: MutableList<Player>): MutableList<Player> {
        if (action == Actions.Raise) {
            return (waitingForActionPlayers + hasDoneActionPlays + currentActionPlayer - foldPlayers).toMutableList()
        }else{
            if (action == Actions.Fold) {
                foldPlayers.add(currentActionPlayer)
            }
            return waitingForActionPlayers
        }
    }


    private fun playerTakeActionInTurn() {
        while (waitingForActionPlayers.firstOrNull() != null) {
            val currentActionPlayer = waitingForActionPlayers.removeFirst()
            val alreadyBetAmountForCurrentPlayer = alreadyBetAmountsMap.getOrDefault(currentActionPlayer, 0)

            val availableActions = calculateAvailableActions(alreadyBetAmountForCurrentPlayer, currentMaximumBetAmounts)
            val minimumActionMounts = currentMaximumBetAmounts - alreadyBetAmountForCurrentPlayer

            val  (action, actionAmounts) = currentActionPlayer.takeAction(availableActions, minimumActionMounts)

            waitingForActionPlayers = rearrangeWaitingList(action, currentActionPlayer, waitingForActionPlayers, hasDoneActionPlays)
            // TODO
            /*
                all-in -> side pot
            */
            pot.amounts += actionAmounts
            currentMaximumBetAmounts = actionAmounts.coerceAtLeast(currentMaximumBetAmounts)

            alreadyBetAmountsMap[currentActionPlayer] = actionAmounts + alreadyBetAmountForCurrentPlayer
            hasDoneActionPlays.remove(currentActionPlayer)
            hasDoneActionPlays.add(currentActionPlayer)
        }
    }

    private fun prepareForNextRound() {
        Rounds.getNextRound(round)?.let {
            round = it
        }
    }

    private fun initWaitingActionList() {
        if (round == Rounds.PreFlop) {
            (players.subList(bigBlindPosition + 1, players.size) + players.subList(0, bigBlindPosition + 1))
                .map { waitingForActionPlayers.add(it) }
        } else {
            (players.subList(buttonPosition + 1, players.size) + players[buttonPosition] - foldPlayers)
                .map {
                    waitingForActionPlayers.add(it)
                }
        }
        hasDoneActionPlays.clear()
        currentMaximumBetAmounts = 0
    }
}