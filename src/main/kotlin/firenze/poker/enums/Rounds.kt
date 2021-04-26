package firenze.poker.enums

enum class Rounds(round: Int) {
    PreFlop(1),
    Flop(2),
    Turn(3),
    River(4);

    companion object {
        fun getNextRound(round: Rounds): Rounds? {
            return when (round) {
                PreFlop -> Flop
                Flop -> Turn
                Turn -> River
                River -> null
            }
        }
    }
}