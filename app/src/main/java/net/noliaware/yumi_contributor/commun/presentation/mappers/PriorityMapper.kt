package net.noliaware.yumi_contributor.commun.presentation.mappers

import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.domain.model.Priority
import net.noliaware.yumi_contributor.commun.domain.model.Priority.*
import javax.inject.Inject

class PriorityMapper @Inject constructor() {

    fun mapPriorityIcon(
        priority: Priority?
    ) = when (priority) {
        WARNING -> R.drawable.ic_warning
        IMPORTANT -> R.drawable.ic_important
        CRITICAL -> R.drawable.ic_critical
        else -> R.drawable.ic_information
    }

    fun mapPriorityTitle(
        priority: Priority?
    ) = when (priority) {
        WARNING -> R.string.priority_warning
        IMPORTANT -> R.string.priority_important
        CRITICAL -> R.string.priority_critical
        else -> R.string.priority_information
    }
}