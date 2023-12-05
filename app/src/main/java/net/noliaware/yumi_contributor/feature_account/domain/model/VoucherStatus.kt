package net.noliaware.yumi_contributor.feature_account.domain.model

enum class VoucherStatus(val value: Int) {
    USABLE(1),
    USED(2),
    CANCELLED(-1),
    INEXISTENT(3);
    companion object {
        fun fromValue(value: Int?) = VoucherStatus.values().firstOrNull { it.value == value }
    }
}

enum class VoucherDeliveryStatus(val value: Int) {
    AVAILABLE(1),
    NON_AVAILABLE(2),
    NON_RETRIEVABLE(3),
    NONE(0);
    companion object {
        fun fromValue(value: Int?) = VoucherDeliveryStatus.values().firstOrNull { it.value == value }
    }
}