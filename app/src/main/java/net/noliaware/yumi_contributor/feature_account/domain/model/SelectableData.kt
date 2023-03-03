package net.noliaware.yumi_contributor.feature_account.domain.model

sealed interface SelectableData<T> {
    data class AssignedData<T>(val data: T? = null) : SelectableData<T>
    data class SelectedData<T>(val data: T? = null) : SelectableData<T>
}
