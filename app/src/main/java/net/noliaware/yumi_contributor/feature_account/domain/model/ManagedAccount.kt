package net.noliaware.yumi_contributor.feature_account.domain.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class ManagedAccount(
    val login: String,
    val title: String,
    val firstName: String,
    val lastName: String,
    val cellPhoneNumber: String,
    val availableVoucherCount: Int,
) : Parcelable