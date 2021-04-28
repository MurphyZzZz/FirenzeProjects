package firenze.poker.model

class Pot{
    var amounts = 0
    var potentialWinners = mutableListOf<Player>()

    private fun compareCards(players: List<Player>): List<Player>{
        // TODO: add compare cards logic
        return emptyList()
    }

    fun distributeAmounts(){
        // TODO: compare the cards
        val winners = compareCards(potentialWinners)
        if (winners.size == 1){
            winners[0].amounts += amounts
        }else {
            winners.forEach{ it.amounts += (amounts / winners.size) }
        }
    }
}
