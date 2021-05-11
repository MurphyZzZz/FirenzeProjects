package firenze.poker.domain

class Deck {
    private val cards = initCards()

    private fun initCards(): MutableList<Card> {
        return Card.ranks.indices.flatMap { i ->
            Card.suits.indices.map { k ->
                Card(rank = i, suit = k)
            }
        }.shuffled().toMutableList()
    }

    fun dealCard(): Card {
        return cards.removeFirst()
    }
}
