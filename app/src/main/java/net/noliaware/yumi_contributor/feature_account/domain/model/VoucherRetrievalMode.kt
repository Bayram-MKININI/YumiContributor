package net.noliaware.yumi_contributor.feature_account.domain.model

enum class VoucherRetrievalMode(val value: Int) {
    BENEFICIARY(1),
    CONTRIBUTOR(2),
    BOTH(3);

    companion object {
        fun fromValue(value: Int?) = VoucherRetrievalMode.values().firstOrNull { it.value == value }
    }
}