package firenze.poker.model

class Play(
    val name: String,
    val amounts: Int
){
    val cards: MutableList<Card> = mutableListOf()

    fun setCard(card: Card){
        cards.add(card)
    }
}
