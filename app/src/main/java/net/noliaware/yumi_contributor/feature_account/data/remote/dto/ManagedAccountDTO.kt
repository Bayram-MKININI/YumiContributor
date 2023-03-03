package net.noliaware.yumi_contributor.feature_account.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import net.noliaware.yumi_contributor.feature_account.domain.model.ManagedAccount

@JsonClass(generateAdapter = true)
data class ManagedAccountDTO(
    @Json(name = "login")
    val login: String,
    @Json(name = "title")
    val title: String,
    @Json(name = "firstName")
    val firstName: String,
    @Json(name = "lastName")
    val lastName: String,
    @Json(name = "cellPhoneNumber")
    val cellPhoneNumber: String?,
    @Json(name = "availableVoucherCount")
    val availableVoucherCount: Int?,
    @Json(name = "userRank")
    val accountRank: Int?,
    @Json(name = "userCount")
    val accountCount: Int?
) {
    fun toManagedAccount() = ManagedAccount(
        login = login,
        title = title,
        firstName = firstName,
        lastName = lastName,
        cellPhoneNumber = cellPhoneNumber ?: "",
        availableVoucherCount = availableVoucherCount ?: 0
    )
}