package net.noliaware.yumi_contributor.feature_profile.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import net.noliaware.yumi_contributor.feature_profile.domain.model.UserProfile

@JsonClass(generateAdapter = true)
data class UserProfileDTO(
    @Json(name = "login")
    val login: String?,
    @Json(name = "title")
    val title: String?,
    @Json(name = "firstName")
    val firstName: String?,
    @Json(name = "lastName")
    val lastName: String?,
    @Json(name = "address")
    val address: String?,
    @Json(name = "addressComplement")
    val addressComplement: String?,
    @Json(name = "postcode")
    val postCode: String?,
    @Json(name = "city")
    val city: String?,
    @Json(name = "country")
    val country: String?,
    @Json(name = "phoneNumber")
    val phoneNumber: String?,
    @Json(name = "cellPhoneNumber")
    val cellPhoneNumber: String?,
    @Json(name = "email")
    val email: String?,
    @Json(name = "employerName")
    val employerName: String?,
    @Json(name = "contributorJob")
    val contributorJob: String?,
    @Json(name = "userCount")
    val userCount: Int?,
    @Json(name = "messageBoxUsagePercentage")
    val messageBoxUsagePercentage: Int
) {
    fun toUserProfile() = UserProfile(
        login = login,
        title = title,
        firstName = firstName,
        lastName = lastName,
        address = address,
        addressComplement = addressComplement,
        postCode = postCode,
        city = city,
        country = country,
        phoneNumber = phoneNumber,
        cellPhoneNumber = cellPhoneNumber,
        email = email,
        employerName = employerName,
        contributorJob = contributorJob,
        userCount = userCount,
        messageBoxUsagePercentage = messageBoxUsagePercentage
    )
}