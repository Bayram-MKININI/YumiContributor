package net.noliaware.yumi_contributor.commun.util

import net.noliaware.yumi_contributor.commun.domain.model.AppMessage

sealed class Resource<T>(
    val data: T? = null,
    val errorType: ErrorType = ErrorType.NONE,
    val appMessage: AppMessage? = null,
    val errorMessage: String? = null
) {
    class Loading<T> : Resource<T>()

    class Success<T>(
        data: T,
        appMessage: AppMessage? = null
    ) : Resource<T>(data = data, appMessage = appMessage)

    class Error<T>(
        errorType: ErrorType,
        errorMessage: String? = null,
        appMessage: AppMessage? = null
    ) : Resource<T>(errorType = errorType, errorMessage = errorMessage, appMessage = appMessage)
}