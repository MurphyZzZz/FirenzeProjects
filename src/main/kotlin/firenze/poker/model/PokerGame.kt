package firenze.poker.model

import firenze.poker.enums.Actions
import firenze.poker.enums.Rounds

class PokerGame(
    val players: List<Player>
) {
    var round = Rounds.PreFlop
    val pots = mutableListOf<Pot>()
    var currentdAmountsInPot = 0
    val communityCards: MutableList<Card> = mutableListOf()
    private val deck = Deck()
    val buttonPosition = 0
    val smallBlindPosition = 1
    val bigBlindPosition = 2

    var waitingForActionPlayers = mutableListOf<Player>()
    val hasDoneActionPlays = mutableListOf<Player>()
    val foldPlayers = mutableListOf<Player>()
    val allInPlayers = mutableListOf<Player>()
    var currentMaximumBetAmounts = 0
    private val alreadyBetAmountsMap = mutableMapOf<Player, Int>()

    var allInFlag = false
    var totalAmountsAfterAllIn = 0
    var allInAmounts = 0
    var allInPlayer: Player? = null

    fun dealCards() {
        players.forEach {
            it.setCard(deck.getCard())
            it.setCard(deck.getCard())
        }
    }

    private fun dealCommunityCard() {
        val getOneCard: () -> Unit = {
            val newCard = deck.getCard()
            newCard.showCard()
            communityCards.add(newCard)
        }
        if (round == Rounds.Flop){
            for (i in 0..2){
                getOneCard()
            }
        } else if(round == Rounds.PreFlop){
            return
        } else {
            getOneCard()
        }
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

    private fun rearrangeWaitingList(
        action: Actions,
        actionAmounts: Int,
        currentActionPlayer: Player,
        minimumActionMounts: Int,
        waitingForActionPlayers: MutableList<Player>,
        hasDoneActionPlays: MutableList<Player>
    ): MutableList<Player> {
        if (action == Actions.Raise) {
            return (waitingForActionPlayers + hasDoneActionPlays - foldPlayers).toMutableList()
        }else if(action == Actions.AllIn && actionAmounts > minimumActionMounts){
            return (waitingForActionPlayers + hasDoneActionPlays + currentActionPlayer - foldPlayers).toMutableList()
        } else{
            if (action == Actions.Fold) {
                foldPlayers.add(currentActionPlayer)
            }
            return waitingForActionPlayers
        }
    }

    private fun handleAllInAction(currentActionPlayer: Player, minimumActionMounts: Int, actionAmounts: Int){
        allInFlag = true
        allInPlayer = currentActionPlayer
        allInPlayers.add(currentActionPlayer)
        if (actionAmounts < minimumActionMounts){
            allInAmounts = currentdAmountsInPot - (minimumActionMounts - actionAmounts) * hasDoneActionPlays.size
        }else{
            allInAmounts = currentdAmountsInPot
        }
    }

    private fun playerTakeActionInTurn() {
        while (waitingForActionPlayers.firstOrNull() != null) {
            val currentActionPlayer = waitingForActionPlayers.removeFirst()
            val alreadyBetAmountForCurrentPlayer = alreadyBetAmountsMap.getOrDefault(currentActionPlayer, 0)

            if (allInFlag){
                if (currentActionPlayer == allInPlayer){
                    val potentialWinners = hasDoneActionPlays - foldPlayers
                    val newPot = Pot()
                    newPot.amounts = totalAmountsAfterAllIn
                    newPot.potentialWinners = potentialWinners
                    pots.add(newPot)
                    currentdAmountsInPot -= totalAmountsAfterAllIn

                    hasDoneActionPlays.remove(allInPlayer)
                    allInFlag = false
                    allInPlayer = null
//                    allInPlayers.clear()
                    allInAmounts = 0
                    continue
                }else{
                    totalAmountsAfterAllIn += allInAmounts
                }
            }

            val availableActions = calculateAvailableActions(alreadyBetAmountForCurrentPlayer, currentMaximumBetAmounts)
            val minimumActionMounts = currentMaximumBetAmounts - alreadyBetAmountForCurrentPlayer

            val (action, actionAmounts) = currentActionPlayer.takeAction(availableActions, minimumActionMounts)
            println("player ${currentActionPlayer.name}, action $action, amounts ${actionAmounts}")

            waitingForActionPlayers = rearrangeWaitingList(action, actionAmounts, currentActionPlayer, minimumActionMounts, waitingForActionPlayers, hasDoneActionPlays)

            if (action == Actions.AllIn) handleAllInAction(currentActionPlayer, minimumActionMounts, actionAmounts)

            currentdAmountsInPot += actionAmounts
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
        val newPot = Pot()
        newPot.amounts = currentdAmountsInPot
        newPot.potentialWinners = (hasDoneActionPlays - foldPlayers)
        pots.add(newPot)
    }

    private fun initWaitingActionList() {
        if (round == Rounds.PreFlop) {
            (players.subList(bigBlindPosition + 1, players.size) + players.subList(0, bigBlindPosition + 1))
                .map { waitingForActionPlayers.add(it) }
        } else {
            (players.subList(buttonPosition + 1, players.size) + players[buttonPosition] - foldPlayers - allInPlayers)
                .map {
                    waitingForActionPlayers.add(it)
                }
        }
        hasDoneActionPlays.clear()
        currentMaximumBetAmounts = 0

        // TODO: if waiting list length is equals to 1, the game finish in advance
    }
}