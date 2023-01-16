package net.noliaware.yumi_contributor.feature_login.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import net.noliaware.yumi_contributor.feature_login.domain.model.InitData

@JsonClass(generateAdapter = true)
class InitDTO(
    @Json(name = "deviceId")
    val deviceId: String = "",
    @Json(name = "keyboard")
    val keyboard: List<Int> = listOf()
) {
    fun toInitData() = InitData(
        deviceId = deviceId,
        keyboard = keyboard
    )
}