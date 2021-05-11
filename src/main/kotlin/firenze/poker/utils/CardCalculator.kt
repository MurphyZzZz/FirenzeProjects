package firenze.poker.utils

import firenze.poker.domain.BestCombination
import firenze.poker.domain.Card
import firenze.poker.domain.Player

class CardCalculator {
    companion object {
        fun getWinners(players: List<Player>, communityCards: List<Card>): List<Set<Player>> {
            val combinationsToPlayers =
                players.associateBy { CardCombinator(it.cards + communityCards).evaluateBestCombination() }
            return divideWinners(combinationsToPlayers)
        }

        private fun divideWinners(combinationsToPlayers: Map<BestCombination, Player>): List<Set<Player>> {
            val rankedCombinations = combinationsToPlayers.keys.sortedWith(this::compareCards).reversed()
            var firstCom = rankedCombinations.first()
            val result = mutableListOf<Set<Player>>()
            var tem = mutableSetOf(combinationsToPlayers[firstCom]!!)
            for (i in 1 until rankedCombinations.size) {
                if (compareCards(firstCom, rankedCombinations[i]) != 0) {
                    result.add(tem.toSet())
                    firstCom = rankedCombinations[i]
                    tem = mutableSetOf(combinationsToPlayers[firstCom]!!)
                } else {
                    tem.add(combinationsToPlayers[rankedCombinations[i]]!!)
                }
            }
            result.add(tem.toSet())
            return result.toList()
        }

        fun compareCards(left: BestCombination, right: BestCombination): Int {
            return when {
                left.name.ordinal > right.name.ordinal -> {
                    1
                }
                left.name.ordinal < right.name.ordinal -> {
                    -1
                }
                else -> {
                    val leftCards = left.cards.sortedBy { it.rank }.map { it.rank }
                    val rightCards = right.cards.sortedBy { it.rank }.map { it.rank }
                    compareRank(leftCards, rightCards)
                }
            }
        }

        private fun compareRank(a: List<Int>, b: List<Int>): Int {
            val newA = a.map { rank -> if (rank == 0) 13 else rank }
            val newB = b.map { rank -> if (rank == 0) 13 else rank }
            for (i in a.indices) {
                if (newA[i] > newB[i]) return 1
                else if (newA[i] < newB[i]) return -1
            }
            return 0
        }
    }
}