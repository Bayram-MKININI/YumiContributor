package net.noliaware.yumi_contributor.feature_alerts.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AlertsDTO(
    @Json(name = "alerts")
    val alertDTOList: List<AlertDTO>
)