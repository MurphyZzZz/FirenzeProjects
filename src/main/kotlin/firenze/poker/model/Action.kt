package firenze.poker.model

import firenze.poker.enums.Actions

data class Action(
    val action: Actions,
    val amounts: Int
)
