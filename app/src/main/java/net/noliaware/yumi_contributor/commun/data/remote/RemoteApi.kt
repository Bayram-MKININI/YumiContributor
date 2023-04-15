package net.noliaware.yumi_contributor.commun.data.remote

import net.noliaware.yumi_contributor.commun.CONNECT
import net.noliaware.yumi_contributor.commun.DELETE_INBOX_MESSAGE
import net.noliaware.yumi_contributor.commun.DELETE_OUTBOX_MESSAGE
import net.noliaware.yumi_contributor.commun.GET_ACCOUNT
import net.noliaware.yumi_contributor.commun.GET_ALERT_LIST
import net.noliaware.yumi_contributor.commun.GET_AVAILABLE_DATA_PER_CATEGORY
import net.noliaware.yumi_contributor.commun.GET_AVAILABLE_VOUCHER_LIST_BY_CATEGORY
import net.noliaware.yumi_contributor.commun.GET_BACK_OFFICE_SIGN_IN_CODE
import net.noliaware.yumi_contributor.commun.GET_CANCELLED_DATA_PER_CATEGORY
import net.noliaware.yumi_contributor.commun.GET_CANCELLED_VOUCHER_LIST_BY_CATEGORY
import net.noliaware.yumi_contributor.commun.GET_FILTER_USERS_LIST
import net.noliaware.yumi_contributor.commun.GET_INBOX_MESSAGE
import net.noliaware.yumi_contributor.commun.GET_INBOX_MESSAGE_LIST
import net.noliaware.yumi_contributor.commun.GET_MANAGED_ACCOUNT_LIST
import net.noliaware.yumi_contributor.commun.GET_OUTBOX_MESSAGE
import net.noliaware.yumi_contributor.commun.GET_OUTBOX_MESSAGE_LIST
import net.noliaware.yumi_contributor.commun.GET_SINGLE_MANAGED_ACCOUNT
import net.noliaware.yumi_contributor.commun.GET_USED_DATA_PER_CATEGORY
import net.noliaware.yumi_contributor.commun.GET_USED_VOUCHER_LIST_BY_CATEGORY
import net.noliaware.yumi_contributor.commun.GET_VOUCHER
import net.noliaware.yumi_contributor.commun.GET_VOUCHER_STATUS
import net.noliaware.yumi_contributor.commun.INIT
import net.noliaware.yumi_contributor.commun.SALT_STRING
import net.noliaware.yumi_contributor.commun.SELECT_USER
import net.noliaware.yumi_contributor.commun.SEND_MESSAGE
import net.noliaware.yumi_contributor.commun.TIMESTAMP
import net.noliaware.yumi_contributor.commun.TOKEN
import net.noliaware.yumi_contributor.commun.USE_VOUCHER
import net.noliaware.yumi_contributor.commun.data.remote.dto.ResponseDTO
import net.noliaware.yumi_contributor.feature_account.data.remote.dto.AvailableVouchersDTO
import net.noliaware.yumi_contributor.feature_account.data.remote.dto.CancelledVouchersDTO
import net.noliaware.yumi_contributor.feature_account.data.remote.dto.FilterAccountsDTO
import net.noliaware.yumi_contributor.feature_account.data.remote.dto.GetVoucherDTO
import net.noliaware.yumi_contributor.feature_account.data.remote.dto.GetVoucherStateDataDTO
import net.noliaware.yumi_contributor.feature_account.data.remote.dto.ManagedAccountsDTO
import net.noliaware.yumi_contributor.feature_account.data.remote.dto.SelectUserDTO
import net.noliaware.yumi_contributor.feature_account.data.remote.dto.SingleManagedAccountDTO
import net.noliaware.yumi_contributor.feature_account.data.remote.dto.UseVoucherResponseDTO
import net.noliaware.yumi_contributor.feature_account.data.remote.dto.UsedVouchersDTO
import net.noliaware.yumi_contributor.feature_account.data.remote.dto.VoucherAvailableCategoriesDTO
import net.noliaware.yumi_contributor.feature_account.data.remote.dto.VoucherCancelledCategoriesDTO
import net.noliaware.yumi_contributor.feature_account.data.remote.dto.VoucherUsedCategoriesDTO
import net.noliaware.yumi_contributor.feature_alerts.data.remote.dto.AlertsDTO
import net.noliaware.yumi_contributor.feature_login.data.remote.dto.AccountDataDTO
import net.noliaware.yumi_contributor.feature_login.data.remote.dto.InitDTO
import net.noliaware.yumi_contributor.feature_message.data.remote.dto.DeleteInboxMessageDTO
import net.noliaware.yumi_contributor.feature_message.data.remote.dto.DeleteOutboxMessageDTO
import net.noliaware.yumi_contributor.feature_message.data.remote.dto.InboxMessageDTO
import net.noliaware.yumi_contributor.feature_message.data.remote.dto.InboxMessagesDTO
import net.noliaware.yumi_contributor.feature_message.data.remote.dto.OutboxMessageDTO
import net.noliaware.yumi_contributor.feature_message.data.remote.dto.OutboxMessagesDTO
import net.noliaware.yumi_contributor.feature_message.data.remote.dto.SentMessageDTO
import net.noliaware.yumi_contributor.feature_profile.data.remote.dto.BOSignInDTO
import net.noliaware.yumi_contributor.feature_profile.data.remote.dto.UserAccountDTO
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Path

interface RemoteApi {

    @FormUrlEncoded
    @POST("$INIT/{$TIMESTAMP}/{$SALT_STRING}/{$TOKEN}")
    suspend fun fetchInitData(
        @Path(TIMESTAMP) timestamp: String,
        @Path(SALT_STRING) saltString: String,
        @Path(TOKEN) token: String,
        @FieldMap params: Map<String, String>
    ): ResponseDTO<InitDTO>

    @FormUrlEncoded
    @POST("$CONNECT/{$TIMESTAMP}/{$SALT_STRING}/{$TOKEN}")
    suspend fun fetchAccountDataForPassword(
        @Path(TIMESTAMP) timestamp: String,
        @Path(SALT_STRING) saltString: String,
        @Path(TOKEN) token: String,
        @FieldMap params: Map<String, String>
    ): ResponseDTO<AccountDataDTO>

    @FormUrlEncoded
    @POST("$GET_FILTER_USERS_LIST/{$TIMESTAMP}/{$SALT_STRING}/{$TOKEN}")
    suspend fun fetchFilterUsersList(
        @Path(TIMESTAMP) timestamp: String,
        @Path(SALT_STRING) saltString: String,
        @Path(TOKEN) token: String,
        @FieldMap params: Map<String, String>
    ): ResponseDTO<FilterAccountsDTO>

    @FormUrlEncoded
    @POST("$GET_MANAGED_ACCOUNT_LIST/{$TIMESTAMP}/{$SALT_STRING}/{$TOKEN}")
    suspend fun fetchManagedAccounts(
        @Path(TIMESTAMP) timestamp: String,
        @Path(SALT_STRING) saltString: String,
        @Path(TOKEN) token: String,
        @FieldMap params: Map<String, String>
    ): ResponseDTO<ManagedAccountsDTO>

    @FormUrlEncoded
    @POST("$GET_SINGLE_MANAGED_ACCOUNT/{$TIMESTAMP}/{$SALT_STRING}/{$TOKEN}")
    suspend fun fetchManagedAccount(
        @Path(TIMESTAMP) timestamp: String,
        @Path(SALT_STRING) saltString: String,
        @Path(TOKEN) token: String,
        @FieldMap params: Map<String, String>
    ): ResponseDTO<SingleManagedAccountDTO>

    @FormUrlEncoded
    @POST("$SELECT_USER/{$TIMESTAMP}/{$SALT_STRING}/{$TOKEN}")
    suspend fun selectUserForId(
        @Path(TIMESTAMP) timestamp: String,
        @Path(SALT_STRING) saltString: String,
        @Path(TOKEN) token: String,
        @FieldMap params: Map<String, String>
    ): ResponseDTO<SelectUserDTO>

    @FormUrlEncoded
    @POST("$GET_AVAILABLE_DATA_PER_CATEGORY/{$TIMESTAMP}/{$SALT_STRING}/{$TOKEN}")
    suspend fun fetchAvailableVoucherDataByCategory(
        @Path(TIMESTAMP) timestamp: String,
        @Path(SALT_STRING) saltString: String,
        @Path(TOKEN) token: String,
        @FieldMap params: Map<String, String>
    ): ResponseDTO<VoucherAvailableCategoriesDTO>

    @FormUrlEncoded
    @POST("$GET_AVAILABLE_VOUCHER_LIST_BY_CATEGORY/{$TIMESTAMP}/{$SALT_STRING}/{$TOKEN}")
    suspend fun fetchAvailableVouchersForCategory(
        @Path(TIMESTAMP) timestamp: String,
        @Path(SALT_STRING) saltString: String,
        @Path(TOKEN) token: String,
        @FieldMap params: Map<String, String>
    ): ResponseDTO<AvailableVouchersDTO>

    @FormUrlEncoded
    @POST("$GET_USED_DATA_PER_CATEGORY/{$TIMESTAMP}/{$SALT_STRING}/{$TOKEN}")
    suspend fun fetchUsedVoucherDataByCategory(
        @Path(TIMESTAMP) timestamp: String,
        @Path(SALT_STRING) saltString: String,
        @Path(TOKEN) token: String,
        @FieldMap params: Map<String, String>
    ): ResponseDTO<VoucherUsedCategoriesDTO>

    @FormUrlEncoded
    @POST("$GET_USED_VOUCHER_LIST_BY_CATEGORY/{$TIMESTAMP}/{$SALT_STRING}/{$TOKEN}")
    suspend fun fetchUsedVouchersForCategory(
        @Path(TIMESTAMP) timestamp: String,
        @Path(SALT_STRING) saltString: String,
        @Path(TOKEN) token: String,
        @FieldMap params: Map<String, String>
    ): ResponseDTO<UsedVouchersDTO>

    @FormUrlEncoded
    @POST("$GET_CANCELLED_DATA_PER_CATEGORY/{$TIMESTAMP}/{$SALT_STRING}/{$TOKEN}")
    suspend fun fetchCancelledVoucherDataByCategory(
        @Path(TIMESTAMP) timestamp: String,
        @Path(SALT_STRING) saltString: String,
        @Path(TOKEN) token: String,
        @FieldMap params: Map<String, String>
    ): ResponseDTO<VoucherCancelledCategoriesDTO>

    @FormUrlEncoded
    @POST("$GET_CANCELLED_VOUCHER_LIST_BY_CATEGORY/{$TIMESTAMP}/{$SALT_STRING}/{$TOKEN}")
    suspend fun fetchCancelledVouchersForCategory(
        @Path(TIMESTAMP) timestamp: String,
        @Path(SALT_STRING) saltString: String,
        @Path(TOKEN) token: String,
        @FieldMap params: Map<String, String>
    ): ResponseDTO<CancelledVouchersDTO>

    @FormUrlEncoded
    @POST("$GET_VOUCHER/{$TIMESTAMP}/{$SALT_STRING}/{$TOKEN}")
    suspend fun fetchVoucherForId(
        @Path(TIMESTAMP) timestamp: String,
        @Path(SALT_STRING) saltString: String,
        @Path(TOKEN) token: String,
        @FieldMap params: Map<String, String>
    ): ResponseDTO<GetVoucherDTO>

    @FormUrlEncoded
    @POST("$GET_VOUCHER_STATUS/{$TIMESTAMP}/{$SALT_STRING}/{$TOKEN}")
    suspend fun fetchVoucherStatusForId(
        @Path(TIMESTAMP) timestamp: String,
        @Path(SALT_STRING) saltString: String,
        @Path(TOKEN) token: String,
        @FieldMap params: Map<String, String>
    ): ResponseDTO<GetVoucherStateDataDTO>

    @FormUrlEncoded
    @POST("$USE_VOUCHER/{$TIMESTAMP}/{$SALT_STRING}/{$TOKEN}")
    suspend fun useVoucher(
        @Path(TIMESTAMP) timestamp: String,
        @Path(SALT_STRING) saltString: String,
        @Path(TOKEN) token: String,
        @FieldMap params: Map<String, String>
    ): ResponseDTO<UseVoucherResponseDTO>

    @FormUrlEncoded
    @POST("$GET_ACCOUNT/{$TIMESTAMP}/{$SALT_STRING}/{$TOKEN}")
    suspend fun fetchAccount(
        @Path(TIMESTAMP) timestamp: String,
        @Path(SALT_STRING) saltString: String,
        @Path(TOKEN) token: String,
        @FieldMap params: Map<String, String>
    ): ResponseDTO<UserAccountDTO>

    @FormUrlEncoded
    @POST("$GET_BACK_OFFICE_SIGN_IN_CODE/{$TIMESTAMP}/{$SALT_STRING}/{$TOKEN}")
    suspend fun fetchBackOfficeSignInCode(
        @Path(TIMESTAMP) timestamp: String,
        @Path(SALT_STRING) saltString: String,
        @Path(TOKEN) token: String,
        @FieldMap params: Map<String, String>
    ): ResponseDTO<BOSignInDTO>

    @FormUrlEncoded
    @POST("$GET_INBOX_MESSAGE_LIST/{$TIMESTAMP}/{$SALT_STRING}/{$TOKEN}")
    suspend fun fetchInboxMessages(
        @Path(TIMESTAMP) timestamp: String,
        @Path(SALT_STRING) saltString: String,
        @Path(TOKEN) token: String,
        @FieldMap params: Map<String, String>
    ): ResponseDTO<InboxMessagesDTO>

    @FormUrlEncoded
    @POST("$GET_INBOX_MESSAGE/{$TIMESTAMP}/{$SALT_STRING}/{$TOKEN}")
    suspend fun fetchInboxMessageForId(
        @Path(TIMESTAMP) timestamp: String,
        @Path(SALT_STRING) saltString: String,
        @Path(TOKEN) token: String,
        @FieldMap params: Map<String, String>
    ): ResponseDTO<InboxMessageDTO>

    @FormUrlEncoded
    @POST("$GET_OUTBOX_MESSAGE_LIST/{$TIMESTAMP}/{$SALT_STRING}/{$TOKEN}")
    suspend fun fetchOutboxMessages(
        @Path(TIMESTAMP) timestamp: String,
        @Path(SALT_STRING) saltString: String,
        @Path(TOKEN) token: String,
        @FieldMap params: Map<String, String>
    ): ResponseDTO<OutboxMessagesDTO>

    @FormUrlEncoded
    @POST("$GET_OUTBOX_MESSAGE/{$TIMESTAMP}/{$SALT_STRING}/{$TOKEN}")
    suspend fun fetchOutboxMessageForId(
        @Path(TIMESTAMP) timestamp: String,
        @Path(SALT_STRING) saltString: String,
        @Path(TOKEN) token: String,
        @FieldMap params: Map<String, String>
    ): ResponseDTO<OutboxMessageDTO>

    @FormUrlEncoded
    @POST("$SEND_MESSAGE/{$TIMESTAMP}/{$SALT_STRING}/{$TOKEN}")
    suspend fun sendMessage(
        @Path(TIMESTAMP) timestamp: String,
        @Path(SALT_STRING) saltString: String,
        @Path(TOKEN) token: String,
        @FieldMap params: Map<String, String>
    ): ResponseDTO<SentMessageDTO>

    @FormUrlEncoded
    @POST("$DELETE_INBOX_MESSAGE/{$TIMESTAMP}/{$SALT_STRING}/{$TOKEN}")
    suspend fun deleteInboxMessageForId(
        @Path(TIMESTAMP) timestamp: String,
        @Path(SALT_STRING) saltString: String,
        @Path(TOKEN) token: String,
        @FieldMap params: Map<String, String>
    ): ResponseDTO<DeleteInboxMessageDTO>

    @FormUrlEncoded
    @POST("$DELETE_OUTBOX_MESSAGE/{$TIMESTAMP}/{$SALT_STRING}/{$TOKEN}")
    suspend fun deleteOutboxMessageForId(
        @Path(TIMESTAMP) timestamp: String,
        @Path(SALT_STRING) saltString: String,
        @Path(TOKEN) token: String,
        @FieldMap params: Map<String, String>
    ): ResponseDTO<DeleteOutboxMessageDTO>

    @FormUrlEncoded
    @POST("$GET_ALERT_LIST/{$TIMESTAMP}/{$SALT_STRING}/{$TOKEN}")
    suspend fun fetchAlertList(
        @Path(TIMESTAMP) timestamp: String,
        @Path(SALT_STRING) saltString: String,
        @Path(TOKEN) token: String,
        @FieldMap params: Map<String, String>
    ): ResponseDTO<AlertsDTO>
}