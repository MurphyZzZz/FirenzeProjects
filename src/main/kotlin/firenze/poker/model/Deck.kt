package firenze.poker.model

import java.util.Collections.shuffle

class Deck {
    private val cards = initCards()

    fun initCards(): MutableList<Card> {
        return Card.ranks.indices.flatMap { i ->
            Card.suits.indices.map { k ->
                Card(rank = i, suit = k)
            }
        }.shuffled().toMutableList()
    }

    fun shuffleCards() {
        shuffle(cards)
    }

    fun getCard(): Card {
        return cards.removeFirst()
    }
}




