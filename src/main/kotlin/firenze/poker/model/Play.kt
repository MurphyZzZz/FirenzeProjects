package firenze.poker.model

data class Play(
    val name: String,
    val amounts: Int,
    val cards: List<Card>
)
