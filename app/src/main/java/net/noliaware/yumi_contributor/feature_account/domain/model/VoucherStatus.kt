package net.noliaware.yumi_contributor.feature_account.domain.model

enum class VoucherStatus(val value: Int) {
    USABLE(1),
    CONSUMED(2),
    CANCELLED(-1),
    INEXISTENT(3);
    companion object {
        fun fromInt(value: Int?) = VoucherStatus.values().firstOrNull { it.value == value }
    }
}