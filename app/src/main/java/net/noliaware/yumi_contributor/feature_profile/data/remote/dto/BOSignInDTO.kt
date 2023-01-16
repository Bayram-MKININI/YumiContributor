package net.noliaware.yumi_contributor.feature_profile.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import net.noliaware.yumi_contributor.feature_profile.domain.model.BOSignIn

@JsonClass(generateAdapter = true)
data class BOSignInDTO(
    @Json(name = "expiryDelayInSeconds")
    val expiryDelayInSeconds: Int,
    @Json(name = "signInCode")
    val signInCode: String
) {
    fun toBOSignIn() = BOSignIn(
        expiryDelayInSeconds = expiryDelayInSeconds,
        signInCode = signInCode
    )
}