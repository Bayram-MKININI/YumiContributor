package net.noliaware.yumi_contributor.feature_profile.domain.model

data class BOSignIn(
    val expiryDelayInSeconds: Int,
    val signInCode: String
)