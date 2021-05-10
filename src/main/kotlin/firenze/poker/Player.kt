package firenze.poker

class Player(val name: String, var money: Int) {

    val cards = mutableListOf<Card>()
    var isActive: Boolean = true
    var isAllIn: Boolean = false
    var totalBid = 0
    var currentRoundBid = 0

    // TODO: two fold more than big blind bid
    fun getRaiseWager(): Int {
        return 0
    }

    fun getAllInWager(): Int {
        return money - totalBid
    }

    fun calculateMoney(bid: Int){
        currentRoundBid += bid
        totalBid += bid
    }

    fun calculateMoneyForCall(bid: Int) {
        val gapBetweenRounds = bid - currentRoundBid
        currentRoundBid += gapBetweenRounds
        totalBid += gapBetweenRounds
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Player

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}
