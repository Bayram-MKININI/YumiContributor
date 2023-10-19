package net.noliaware.yumi_contributor.commun.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import net.noliaware.yumi_contributor.commun.domain.model.Action
import net.noliaware.yumi_contributor.commun.domain.model.ActionParam

@JsonClass(generateAdapter = true)
data class ActionDTO(
    @Json(name = "type")
    val type: String = "",
    @Json(name = "params")
    val params: List<ActionParam>
) {
    fun toAction() = Action(
        type = type,
        params = params
    )
}