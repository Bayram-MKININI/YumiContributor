package net.noliaware.yumi_contributor.feature_login.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import net.noliaware.yumi_contributor.feature_login.domain.model.AccountData

@JsonClass(generateAdapter = true)
data class AccountDataDTO(
    @Json(name = "welcomeMessage")
    val helloMessage: String?,
    @Json(name = "welcomeUser")
    val userName: String?,
    @Json(name = "userCount")
    val accountCount: Int?,
    @Json(name = "encryptionVector")
    val encryptionVector: String?,
    @Json(name = "messageSubjects")
    val messageSubjectDTOs: List<MessageSubjectDTO> = listOf(),
    @Json(name = "newAlertCount")
    val newAlertCount: Int?,
    @Json(name = "newMessageCount")
    val newMessageCount: Int?
) {
    fun toAccountData() = AccountData(
        helloMessage = helloMessage ?: "",
        userName = userName ?: "",
        accountCount = accountCount ?: 0,
        messageSubjects = messageSubjectDTOs.map { it.toMessageSubject() },
        newAlertCount = newAlertCount ?: 0,
        newMessageCount = newMessageCount ?: 0
    )
}