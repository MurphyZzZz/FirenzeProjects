package firenze.poker.model

data class PokerGame(
    val plays: List<Play>,
    val round: Round,
    val pot: Pot,
    val communityCards: List<Card>,
    val button: Play,
    val smallBlind: Play,
    val bigBlind: Play,
)