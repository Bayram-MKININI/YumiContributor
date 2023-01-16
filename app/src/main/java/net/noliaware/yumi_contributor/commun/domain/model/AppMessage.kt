package net.noliaware.yumi_contributor.commun.domain.model

class AppMessage(
    val type: AppMessageType?,
    val level: AppMessageLevel?,
    val title: String,
    val body: String
)

enum class AppMessageType(val value: String) {
    POPUP("popup"),
    SNACKBAR("snackbar"),
    TOAST("toast");

    companion object {
        @JvmStatic
        fun fromString(value: String) = values().find { response -> response.value == value }
    }
}

enum class AppMessageLevel(val value: Int) {
    INFORMATION(1),
    WARNING(2),
    ALERT(3),
    ERROR(4);

    companion object {
        @JvmStatic
        fun fromInt(value: Int) = values().find { response -> response.value == value }
    }
}