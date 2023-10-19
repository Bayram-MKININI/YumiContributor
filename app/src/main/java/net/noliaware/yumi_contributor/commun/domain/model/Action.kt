package net.noliaware.yumi_contributor.commun.domain.model

data class Action(
    val type: String = "",
    val params: List<ActionParam>
)