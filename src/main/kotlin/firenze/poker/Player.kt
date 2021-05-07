package firenze.poker

class Player(val name: String) {

    // TODO: two fold more than big blind bid
    fun getRaiseWager(): Int {
        return 0
    }

    // TODO: return left money
    fun getAllInWager(): Int {
        return 0
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
