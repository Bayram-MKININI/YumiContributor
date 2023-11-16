package net.noliaware.yumi_contributor.commun.util

import net.noliaware.yumi_contributor.commun.domain.model.AppMessage

sealed interface UIEvent {
    data class ShowAppMessage(val appMessage: AppMessage) : UIEvent
    data class ShowError(val errorUI: ErrorUI) : UIEvent
}
