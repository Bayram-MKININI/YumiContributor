package net.noliaware.yumi_contributor.feature_alerts.domain.model

import net.noliaware.yumi_contributor.commun.domain.model.Priority

data class Alert(
    val alertId: String,
    val alertDate: String,
    val alertTime: String,
    val alertPriority: Priority?,
    val alertText: String
)