package net.noliaware.yumi_contributor.commun.domain.repository

import net.noliaware.yumi_contributor.commun.domain.model.Action


interface ActionsRepository {
    fun performActions(actions: List<Action>)
}