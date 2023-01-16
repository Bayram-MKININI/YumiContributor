package net.noliaware.yumi_contributor.feature_login.domain.model

import java.io.Serializable

data class AccountData(
    val messageSubjects: List<MessageSubject>,
    val newAlertCount: Int = 0,
    val newMessageCount: Int = 0
) : Serializable