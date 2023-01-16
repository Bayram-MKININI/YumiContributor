package net.noliaware.yumi_contributor.feature_login.domain.model

import java.io.Serializable

data class MessageSubject(
    val subjectId: Int,
    val subjectLabel: String
) : Serializable