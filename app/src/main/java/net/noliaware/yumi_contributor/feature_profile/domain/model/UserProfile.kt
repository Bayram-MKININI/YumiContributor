package net.noliaware.yumi_contributor.feature_profile.domain.model

import java.io.Serializable

data class UserProfile(
    val login: String?,
    val title: String?,
    val firstName: String?,
    val lastName: String?,
    val address: String?,
    val addressComplement: String?,
    val postCode: String?,
    val city: String?,
    val country: String?,
    val phoneNumber: String?,
    val cellPhoneNumber: String?,
    val email: String?,
    val employerName: String?,
    val contributorJob: String?,
    val userCount: Int?,
    val messageBoxUsagePercentage: Int
) : Serializable