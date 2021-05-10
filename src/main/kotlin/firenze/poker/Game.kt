package firenze.poker

import Round
import firenze.poker.enums.Rounds

class Game(vararg val players: Player) {
    var round = Round(*players)
    var currentRoundName: Rounds = Rounds.PREFLOP
    var end = false
    var totalWager = 0
    private val deck = Deck()
    val communityCards = mutableListOf<Card>()

    fun execute(action: Action){
        round.execute(action)
        if (round.nextRound()){
            prepareForNextRound()
        }
    }

    private fun endGame(){
        end = true
        val winners = shutDown()
        WagerCalculator.splitWager(this, winners)
    }

    private fun shutDown(): List<Set<Player>> {
        return emptyList()
    }

    private fun prepareForNextRound(){
        totalWager += round.pot

        val nextRoundIndex = currentRoundName.ordinal + 1

        val activePlayers = players.filter { it.isActive }.toTypedArray()

        if ((nextRoundIndex >= Rounds.values().size) || (activePlayers.size < 2)){
            endGame()
        } else {
            currentRoundName = Rounds.values()[nextRoundIndex]
            activePlayers.forEach { it.currentRoundBid = 0 }
            round = Round(*activePlayers)
            dealCommunityCards()
        }
    }

    private fun dealCommunityCards() {
        if (currentRoundName == Rounds.FLOP){
            for (i in 0..2){
                communityCards.add(deck.dealCard())
            }
        } else {
            communityCards.add(deck.dealCard())
        }
    }

    fun dealCards() {
        players.forEach { player ->
                    player.cards.add(deck.dealCard())
                    player.cards.add(deck.dealCard())
        }
    }
}