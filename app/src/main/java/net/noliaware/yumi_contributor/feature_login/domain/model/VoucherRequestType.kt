package net.noliaware.yumi_contributor.feature_login.domain.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class VoucherRequestType(
    val requestTypeId: Int,
    val requestTypeLabel: String
) : Parcelable