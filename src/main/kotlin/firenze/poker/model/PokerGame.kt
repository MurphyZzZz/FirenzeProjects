package firenze.poker.model

class PokerGame(
    val plays: List<Play>
){
    val round = Round(name = "Pre-flop")
    val pot = Pot(amounts = 0)
    val communityCards: List<Card> = mutableListOf<Card>()
    val deck = Deck()
    val buttonPosition = 0
    val smallBlindPosition = 1
    val bigBlindPosition = 2
}