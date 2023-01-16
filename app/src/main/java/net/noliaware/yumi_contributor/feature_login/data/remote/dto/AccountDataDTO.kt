package net.noliaware.yumi_contributor.feature_login.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import net.noliaware.yumi_contributor.feature_login.domain.model.AccountData

@JsonClass(generateAdapter = true)
data class AccountDataDTO(
    @Json(name = "encryptionVector")
    val encryptionVector: String = "",
    @Json(name = "messageSubjects")
    val messageSubjectDTOs: List<MessageSubjectDTO> = listOf(),
    @Json(name = "newAlertCount")
    val newAlertCount: Int = 0,
    @Json(name = "newMessageCount")
    val newMessageCount: Int = 0
) {
    fun toAccountData() = AccountData(
        messageSubjects = messageSubjectDTOs.map { it.toMessageSubject() },
        newAlertCount = newAlertCount,
        newMessageCount = newMessageCount
    )
}