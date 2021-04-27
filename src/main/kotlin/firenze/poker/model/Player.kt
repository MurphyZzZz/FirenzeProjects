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

    fun takeAction(availableActions: List<Actions>, minimumActionMounts: Int): Action {
        // TODO: mock play's action and reduce player's amount
        // TODO: validate actions, such check and fold's amounts only can be 0
        println("You have listed available actions: ${availableActions.joinToString(",")}, and minimumActionMounts is $minimumActionMounts")
        return Action(action = Actions.Bet, amounts = 10)
    }
}
