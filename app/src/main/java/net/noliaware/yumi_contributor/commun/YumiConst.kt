package net.noliaware.yumi_contributor.commun

import net.noliaware.yumi_contributor.BuildConfig

object ApiConstants {
    const val BASE_ENDPOINT = "https://api.noliaware.net/yumi/contributor/"
    const val INIT = "init"
    const val CONNECT = "connect"
    const val SET_PRIVACY_POLICY_READ_STATUS = "setPrivacyPolicyReadStatus"
    const val GET_FILTER_USERS_LIST = "getUserSearchItemList"
    const val GET_MANAGED_ACCOUNT_LIST = "getUserAccountList"
    const val GET_SINGLE_MANAGED_ACCOUNT = "getUserAccount"
    const val SELECT_USER = "selectUserAccount"
    const val GET_AVAILABLE_DATA_PER_CATEGORY = "getUserAvailableVoucherDataPerCategory"
    const val GET_AVAILABLE_VOUCHER_LIST_BY_CATEGORY = "getUserAvailableVoucherListByCategory"
    const val GET_USED_DATA_PER_CATEGORY = "getUserUsedVoucherDataPerCategory"
    const val GET_USED_VOUCHER_LIST_BY_CATEGORY = "getUserUsedVoucherListByCategory"
    const val GET_CANCELLED_DATA_PER_CATEGORY = "getUserCanceledVoucherDataPerCategory"
    const val GET_CANCELLED_VOUCHER_LIST_BY_CATEGORY = "getUserCanceledVoucherListByCategory"
    const val GET_VOUCHER = "getUserVoucher"
    const val GET_VOUCHER_STATUS = "getUserVoucherStatus"
    const val USE_VOUCHER = "useUserVoucher"
    const val GET_ACCOUNT = "getAccount"
    const val GET_BACK_OFFICE_SIGN_IN_CODE = "getBackOfficeSignInCode"
    const val GET_ALERT_LIST = "getAlertList"
    const val GET_INBOX_MESSAGE_LIST = "getInboxMessageList"
    const val GET_INBOX_MESSAGE = "getInboxMessage"
    const val GET_OUTBOX_MESSAGE_LIST = "getOutboxMessageList"
    const val GET_OUTBOX_MESSAGE = "getOutboxMessage"
    const val SEND_MESSAGE = "sendMessage"
    const val DELETE_INBOX_MESSAGE = "delInboxMessage"
    const val DELETE_OUTBOX_MESSAGE = "delOutboxMessage"
}

object ApiParameters {
    const val LOGIN = "login"
    const val PASSWORD = "password"
    const val APP_VERSION = "appVersion"
    const val DEVICE_ID = "deviceId"
    const val PUSH_TOKEN = "devicePushToken"
    const val DEVICE_TYPE = "deviceType"
    const val DEVICE_OS = "deviceOS"
    const val DEVICE_UUID = "deviceUUID"
    const val DEVICE_LABEL = "deviceLabel"
    const val SESSION_ID = "sessionId"
    const val SESSION_TOKEN = "sessionToken"
    const val TIMESTAMP = "timestamp"
    const val SALT_STRING = "saltString"
    const val TOKEN = "token"
    const val USER_ID = "userId"
    const val SELECTED_USER = "selectedUser"
    const val CATEGORY_ID = "categoryId"
    const val VOUCHER_ID = "voucherId"
    const val VOUCHER_CODE_DATA = "voucherCodeData"
    const val LIMIT = "limit"
    const val LIST_PAGE_SIZE = 20
    const val OFFSET = "offset"
    const val MESSAGE_ID = "messageId"
    const val MESSAGE_PRIORITY = "messagePriority"
    const val MESSAGE_SUBJECT_ID = "messageSubjectId"
    const val MESSAGE_BODY = "messageBody"
    const val TIMESTAMP_OFFSET = "timestampOffset"
}

object ActionTypes {
    const val DELETE_CACHED_DEVICE_ID = "delete_cached_device_id"
    const val MONITOR = "monitor"
}

object Args {
    const val MANAGED_ACCOUNT = "managedAccount"
    const val SELECTED_CATEGORY = "selectedCategory"
    const val DATA_SHOULD_REFRESH = "dataShouldRefresh"
}

object Push {
    const val ACTION_PUSH_DATA = BuildConfig.APPLICATION_ID + ".action.PUSH"
    const val PUSH_TITLE = "title"
    const val PUSH_BODY = "body"
}

object DateTime {
    const val DATE_TIME_SOURCE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSSSSS"
    const val DATE_SOURCE_FORMAT = "yyyy-MM-dd"
    const val TIME_SOURCE_FORMAT = "HH:mm:ss"
    const val NUMERICAL_DATE_FORMAT = "dd/MM/yyyy"
    const val SINGLE_DAY_DATE_FORMAT = "EEE"
    const val DAY_OF_MONTH_NUMERICAL_DATE_FORMAT = "dd/MM"
    const val LONG_DATE_WITH_DAY_FORMAT = "EEEE dd LLLL yyyy"
    const val SHORT_DATE_FORMAT = "dd LLL yyyy"
    const val HOURS_TIME_FORMAT = "HH:mm"
    const val MINUTES_TIME_FORMAT = "mm:ss"
}

object RemoteConfig {
    const val KEY_CURRENT_VERSION = "android_force_update_current_version"
    const val KEY_FORCE_UPDATE_REQUIRED = "android_force_update_required"
    const val KEY_FORCE_UPDATE_URL = "android_force_update_store_url"
}

object FragmentKeys {
    const val AVAILABLE_VOUCHERS_LIST_REQUEST_KEY = "available_vouchers_list_request_key"
    const val VOUCHER_DETAILS_REQUEST_KEY = "voucher_details_request_key"
    const val QR_CODE_REQUEST_KEY = "qr_code_request_key"
    const val VOUCHER_ID_RESULT_KEY = "voucher_id_result_key"
    const val REFRESH_RECEIVED_MESSAGES_REQUEST_KEY = "refresh_received_message_request_key"
    const val REFRESH_SENT_MESSAGES_REQUEST_KEY = "refresh_sent_message_request_key"
}

object UI {
    const val GOLDEN_RATIO = 1.6180339887
}