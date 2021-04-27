package firenze.poker.model

data class Card(
    val rank: Int,
    val suit: Int
){
    companion object {
        val ranks = arrayOf("Ace", "2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King")
        val suits = arrayOf("Diamonds", "Clubs", "Hearts", "Spades")
    }
}

fun Card.showCard(){
    println("This card's rank - ${Card.ranks[this.rank]} and suit - ${Card.suits[this.suit]}")
}
