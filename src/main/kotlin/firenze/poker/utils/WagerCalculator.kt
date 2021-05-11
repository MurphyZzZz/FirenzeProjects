package firenze.poker.utils

import firenze.poker.domain.Game
import firenze.poker.domain.Player

class WagerCalculator {
    companion object {
        fun splitWager(game: Game, winners: List<Set<Player>>) {
            cutPlayerBid(game)
            calculateWager(game, winners)
        }

        private fun cutPlayerBid(game: Game) {
            game.players.forEach { player -> player.money -= player.totalBid }
        }

        private fun calculateWager(game: Game, winnerSets: List<Set<Player>>) {
            // if there only one winner
            val winners = winnerSets.flatten()
            if (winners.size == 1){
                val playersForOneSet = winnerSets.first()
                val player = playersForOneSet.first()
                player.money += game.totalWager
                return
            }
            // if more than one winner: A > B > C
            // if more than one winner with same cards
            winnerSets.forEach { calculateWagerInEachSidePot(game, it) }
        }

        private fun calculateWagerInEachSidePot(game: Game, winners: Set<Player>){
            if (game.totalWager <= 0) {
                return
            }

            val totalNumberOfPlayers = game.players.size

            if (winners.size == 1){
                val player = winners.first()
                if ((player.totalBid * totalNumberOfPlayers) <= game.totalWager) {
                    val wager = player.totalBid * totalNumberOfPlayers
                    player.money += wager
                    game.totalWager -= wager
                } else {
                    player.money += game.totalWager
                    game.totalWager = 0
                }
            } else {
                val player = winners.first()
                // (A, B)(all-in players), (C)(final player)
                if ((player.totalBid * totalNumberOfPlayers) <= game.totalWager){
                    winners.forEach { it.money += (player.totalBid * totalNumberOfPlayers / 2) }
                    game.totalWager -= (player.totalBid * totalNumberOfPlayers)
                } else {
                    // (A, B)(final players), (C)(all-in)
                    val eachCut = game.totalWager / winners.size
                    winners.forEach { it.money += eachCut }
                    game.totalWager = 0
                }
            }
        }
    }
}