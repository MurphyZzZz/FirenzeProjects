package firenze.poker.utils

import firenze.poker.domain.Card
import firenze.poker.enums.Combinations
import firenze.poker.exception.NoBestCardsException


class CardCombinator(cards: Set<Card>) {

    companion object {
        val rankSize = Card.ranks.size
        val suitSize = Card.suits.size
    }


    private val availableCards = cards.toMutableList()
    private val rankRecords = IntArray(rankSize) { rankSize * (0) }
    private val suitRecords = IntArray(suitSize) { suitSize * (0) }


    private fun sortByRankWithAce(): List<Int> {
        val ranks = availableCards.sortedByDescending { it.rank }.map { it.rank }.toMutableList()
        if (getBiggestRankCard().rank == 0){
            ranks.removeLast()
            ranks.add(0, 0)
        }
        return ranks.toList()
    }

    private fun getBiggestRankCard(): Card {
        return availableCards.firstOrNull { it.rank == 0 } ?: availableCards.last()
    }

    private fun sortByRankThenSuit() {
        availableCards.sortWith(compareBy(Card::rank, Card::suit))
    }

    fun evaluateHand(): Pair<Combinations, List<Card>> {

        availableCards.forEach {
            rankRecords[it.rank]++
            suitRecords[it.suit]++
        }

        sortByRankThenSuit()

        val evaluator = listOf(
            ::evaluateRoyalFlush,
            ::evaluateStraightFlush,
            ::evaluateFourOfAKind,
            ::evaluateFullHouse,
            ::evaluateFlush,
            ::evaluateStraight,
            ::evaluateThreeOfAKind,
            ::evaluateTwoPair,
            ::evaluateOnePair,
            ::evaluateHighCard,
        )

        for (e in evaluator) {
            val value = e.invoke()
            if (value != null) {
                return value
            }
        }

        throw NoBestCardsException()
    }

    private fun evaluateRoyalFlush(): Pair<Combinations, List<Card>>? {

        val royalFlushRankIndex = intArrayOf(9, 10, 11, 12, 0)
        val ranks = royalFlushRankIndex.map { rankRecords[it] }
        val bestCards = mutableListOf<Card>()
        if (ranks.all { it == 1 } && suitRecords.any { it > 4 }) {
            // ace could be at position 0, 1, 2
            for (i in 0..2) {
                if (availableCards[i].rank == 0) {
                    // the remaining 4 cards could be start with position 1, 2, 3
                    bestCards.add(availableCards[i])
                    for (j in 1 until 4 - i) {
                        if (availableCards[i + j].rank == 9 && availableCards[i + j + 1].rank == 10 && availableCards[i + j + 2].rank == 11 && availableCards[i + j + 3].rank == 12
                            &&
                            availableCards[i].suit == availableCards[i + j].suit && availableCards[i].suit == availableCards[i + j + 1].suit && availableCards[i].suit == availableCards[i + j + 2].suit && availableCards[i].suit == availableCards[i + j + 3].suit
                        ) {
                            (j..(j + 3)).map { bestCards.add(availableCards[i + it]) }
                            return Combinations.ROYAL_FLUSH to bestCards
                        }
                    }
                }
            }
        }
        return null
    }

    private fun evaluateStraightFlush(): Pair<Combinations, List<Card>>? {
        if (suitRecords.any { it > 4 }) {
            for (i in availableCards.size - 1 downTo 4) {
                if (availableCards[i].rank - 1 == availableCards[i - 1].rank && availableCards[i].rank - 2 == availableCards[i - 2].rank && availableCards[i].rank - 3 == availableCards[i - 3].rank && availableCards[i].rank - 4 == availableCards[i - 4].rank
                    &&
                    availableCards[i].suit == availableCards[i - 1].suit && availableCards[i].suit == availableCards[i - 2].suit && availableCards[i].suit == availableCards[i - 3].suit && availableCards[i].suit == availableCards[i - 4].suit
                ) {
                    val bestCards = ((i - 4)..i).map { availableCards[it] }
                    return Combinations.STRAIGHT_FLUSH to bestCards
                }
            }
        }
        return null
    }

    private fun evaluateFourOfAKind(): Pair<Combinations, List<Card>>? {
        for (i in rankRecords.indices) {
            if (rankRecords[i] == 4) {
                val bestCards = availableCards.filter { it.rank == i } + getBiggestRankCard()
                return Combinations.FOUR_OF_A_KIND to bestCards
            }
        }
        return null
    }

    private fun evaluateFullHouse(): Pair<Combinations, List<Card>>? {
        var threeOfKindRankIndex = -1
        var twoOfKindRankIndex = -1
        for (i in (rankRecords.size - 1) downTo 0) {
            if (threeOfKindRankIndex < 0 || twoOfKindRankIndex < 0) {
                if (rankRecords[i] > 2) {
                    threeOfKindRankIndex = i
                } else if (rankRecords[i] > 1) {
                    twoOfKindRankIndex = i
                }
            } else {
                break
            }
        }
        if (threeOfKindRankIndex >= 0 && twoOfKindRankIndex >= 0) {
            val bestCards = availableCards.filter { it.rank == threeOfKindRankIndex } + availableCards.filter { it.rank == twoOfKindRankIndex }
            return Combinations.FULL_HOUSE to bestCards
        }
        return null
    }

    private fun evaluateFlush(): Pair<Combinations, List<Card>>? {
        if (suitRecords.any { it > 4 }) {
            for (i in (availableCards.size - 1) downTo 4) {
                if (availableCards[i].suit == availableCards[i - 1].suit && availableCards[i].suit == availableCards[i - 2].suit && availableCards[i].suit == availableCards[i - 3].suit && availableCards[i].suit == availableCards[i - 4].suit) {
                    val bestCards = ((i - 4)..i).map { availableCards[it] }
                    return Combinations.FLUSH to bestCards
                }
            }
        }
        return null
    }

    private fun evaluateStraight(): Pair<Combinations, List<Card>>? {

        for (i in (rankRecords.size - 1) downTo 5) {
            if (rankRecords[i] > 0 && rankRecords[i - 1] > 0 && rankRecords[i - 2] > 0 && rankRecords[i - 3] > 0 && rankRecords[i - 4] > 0
            ) {
                val firstIndex = availableCards.indexOfFirst { it.rank == i }
                val bestCards = ((firstIndex - 4) .. firstIndex).map { availableCards[it] }
                return Combinations.STRAIGHT to bestCards
            }
        }
        return null
    }

    private fun evaluateThreeOfAKind(): Pair<Combinations, List<Card>>? {
        for (i in (rankRecords.size - 1) downTo 0) {
            if (rankRecords[i] > 2) {
                val firstIndex = availableCards.indexOfFirst { it.rank == i }
                val firstTwo = sortByRankWithAce().filter { it != i }.subList(0,2)
                val firstTwoCards = availableCards.filter { firstTwo.contains(it.rank) }
                val bestCards = (firstIndex .. (firstIndex + 2)).map { availableCards[it] } + firstTwoCards
                return Combinations.THREE_OF_A_KIND to bestCards
            }
        }
        return null
    }

    private fun evaluateTwoPair(): Pair<Combinations, List<Card>>? {
        var firstPairRankIndex = -1
        var secondPairRankIndex = -1
        for (i in (rankRecords.size - 1) downTo 0) {
            if (firstPairRankIndex < 0 || secondPairRankIndex < 0) {
                if (rankRecords[i] > 1 && firstPairRankIndex < 0) {
                    firstPairRankIndex = i
                } else if (rankRecords[i] > 1) {
                    secondPairRankIndex = i
                }
            } else {
                break
            }
        }

        if (firstPairRankIndex >= 0 && secondPairRankIndex >= 0) {
            val first = sortByRankWithAce().first { it != firstPairRankIndex && it != secondPairRankIndex }
            val firstCards = availableCards.filter { first == it.rank }
            val bestCards = availableCards.filter { it.rank == firstPairRankIndex } + availableCards.filter { it.rank == secondPairRankIndex } + firstCards
            return Combinations.TWO_PAIR to bestCards

        }
        return null
    }

    private fun evaluateOnePair(): Pair<Combinations, List<Card>>? {
        for (i in (rankRecords.size - 1) downTo 0) {
            if (rankRecords[i] > 1) {
                val firstThree = sortByRankWithAce().filter { it != i }.subList(0, 3)
                val firstThreeCards = availableCards.filter { firstThree.contains(it.rank) }
                val bestCards = availableCards.filter { it.rank == i } + firstThreeCards
                return Combinations.ONE_PAIR to bestCards
            }
        }
        return null
    }

    private fun evaluateHighCard(): Pair<Combinations, List<Card>> {
        val bestCards = listOf(getBiggestRankCard()) + ((availableCards.size - 1) downTo (availableCards.size - 4)).map { availableCards[it] }
        return Combinations.HIGH_CARD to bestCards
    }
}