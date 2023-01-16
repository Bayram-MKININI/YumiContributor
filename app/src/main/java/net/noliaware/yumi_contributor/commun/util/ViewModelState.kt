package net.noliaware.yumi_contributor.commun.util

sealed interface ViewModelState<T> {
    data class DataState<T>(val data: T? = null) : ViewModelState<T>
    class LoadingState<T> : ViewModelState<T>
}
