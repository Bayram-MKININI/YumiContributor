package net.noliaware.yumi_contributor.commun.util

import androidx.annotation.StringRes

sealed interface ErrorUI {
    data object ErrUINone : ErrorUI
    data class ErrUISystem(
        val errorMessage: String? = null,
        @StringRes val errorStrRes: Int? = null
    ) : ErrorUI
    data class ErrUINetwork(@StringRes val errorStrRes: Int) : ErrorUI
}