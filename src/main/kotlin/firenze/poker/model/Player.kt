package firenze.poker.model

import firenze.poker.enums.Actions

class Player(
    val name: String,
    val amounts: Int
){
    val cards: MutableList<Card> = mutableListOf()

    fun setCard(card: Card){
        cards.add(card)
    }

    fun takeAction(availableActions: MutableList<Actions>, currentMaximumBetAmounts: Int): Action {
        // TODO: mock play's action
        return Action(action = Actions.Bet, amounts = 10)
    }
}
