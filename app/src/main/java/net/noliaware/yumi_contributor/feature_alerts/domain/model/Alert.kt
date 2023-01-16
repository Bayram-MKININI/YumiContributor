package net.noliaware.yumi_contributor.feature_alerts.domain.model

data class Alert(
    val alertId: String,
    val alertDate: String,
    val alertTime: String,
    val alertLevel: Int,
    val alertText: String,
)