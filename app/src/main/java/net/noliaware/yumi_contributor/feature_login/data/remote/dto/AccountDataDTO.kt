package net.noliaware.yumi_contributor.feature_login.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import net.noliaware.yumi_contributor.feature_login.domain.model.AccountData
import net.noliaware.yumi_contributor.feature_login.domain.model.TFAMode

@JsonClass(generateAdapter = true)
data class AccountDataDTO(
    @Json(name = "privacyPolicyUrl")
    val privacyPolicyUrl: String = "",
    @Json(name = "privacyPolicyReadStatus")
    val privacyPolicyReadStatus: Int,
    @Json(name = "welcomeMessage")
    val helloMessage: String?,
    @Json(name = "welcomeUser")
    val userName: String?,
    @Json(name = "userCount")
    val accountCount: Int?,
    @Json(name = "voucherRequestTypes")
    val voucherRequestTypeDTOs: List<VoucherRequestTypeDTO> = listOf(),
    @Json(name = "encryptionVector")
    val encryptionVector: String?,
    @Json(name = "messageSubjects")
    val messageSubjectDTOs: List<MessageSubjectDTO> = listOf(),
    @Json(name = "newAlertCount")
    val newAlertCount: Int?,
    @Json(name = "newMessageCount")
    val newMessageCount: Int?,
    @Json(name = "twoFactorAuthMode")
    val twoFactorAuthMode: Int = 0
) {
    fun toAccountData() = AccountData(
        privacyPolicyUrl = privacyPolicyUrl,
        shouldConfirmPrivacyPolicy = privacyPolicyReadStatus == 0,
        helloMessage = helloMessage ?: "",
        userName = userName ?: "",
        accountCount = accountCount ?: 0,
        voucherRequestTypes = voucherRequestTypeDTOs.map { it.toVoucherRequestType() },
        messageSubjects = messageSubjectDTOs.map { it.toMessageSubject() },
        newAlertCount = newAlertCount ?: 0,
        newMessageCount = newMessageCount ?: 0,
        twoFactorAuthMode = TFAMode.fromInt(twoFactorAuthMode) ?: TFAMode.NONE
    )
}