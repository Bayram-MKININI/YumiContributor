package net.noliaware.yumi_contributor.commun.data.remote

import net.noliaware.yumi_contributor.commun.CONNECT
import net.noliaware.yumi_contributor.commun.DELETE_INBOX_MESSAGE
import net.noliaware.yumi_contributor.commun.DELETE_OUTBOX_MESSAGE
import net.noliaware.yumi_contributor.commun.GET_ACCOUNT
import net.noliaware.yumi_contributor.commun.GET_ALERT_LIST
import net.noliaware.yumi_contributor.commun.GET_AVAILABLE_VOUCHER_LIST_BY_CATEGORY
import net.noliaware.yumi_contributor.commun.GET_BACK_OFFICE_SIGN_IN_CODE
import net.noliaware.yumi_contributor.commun.GET_DATA_PER_CATEGORY
import net.noliaware.yumi_contributor.commun.GET_INBOX_MESSAGE
import net.noliaware.yumi_contributor.commun.GET_INBOX_MESSAGE_LIST
import net.noliaware.yumi_contributor.commun.GET_MANAGED_ACCOUNT_LIST
import net.noliaware.yumi_contributor.commun.GET_OUTBOX_MESSAGE
import net.noliaware.yumi_contributor.commun.GET_OUTBOX_MESSAGE_LIST
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
import net.noliaware.yumi_contributor.feature_account.data.remote.dto.ManagedAccountsDTO
import net.noliaware.yumi_contributor.feature_account.data.remote.dto.GetVoucherDTO
import net.noliaware.yumi_contributor.feature_account.data.remote.dto.GetVoucherStatusDTO
import net.noliaware.yumi_contributor.feature_account.data.remote.dto.SelectUserDTO
import net.noliaware.yumi_contributor.feature_account.data.remote.dto.UseVoucherResponseDTO
import net.noliaware.yumi_contributor.feature_account.data.remote.dto.VoucherCategoriesDTO
import net.noliaware.yumi_contributor.feature_account.data.remote.dto.VouchersDTO
import net.noliaware.yumi_contributor.feature_alerts.data.remote.dto.AlertsDTO
import net.noliaware.yumi_contributor.feature_login.data.remote.dto.AccountDataDTO
import net.noliaware.yumi_contributor.feature_login.data.remote.dto.InitDTO
import net.noliaware.yumi_contributor.feature_message.data.remote.dto.DeleteMessageDTO
import net.noliaware.yumi_contributor.feature_message.data.remote.dto.MessagesDTO
import net.noliaware.yumi_contributor.feature_message.data.remote.dto.SentMessageDTO
import net.noliaware.yumi_contributor.feature_message.data.remote.dto.SingleMessageDTO
import net.noliaware.yumi_contributor.feature_profile.data.remote.dto.BOSignInDTO
import net.noliaware.yumi_contributor.feature_profile.data.remote.dto.UsedVouchersDTO
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
    @POST("$GET_MANAGED_ACCOUNT_LIST/{$TIMESTAMP}/{$SALT_STRING}/{$TOKEN}")
    suspend fun fetchManagedAccounts(
        @Path(TIMESTAMP) timestamp: String,
        @Path(SALT_STRING) saltString: String,
        @Path(TOKEN) token: String,
        @FieldMap params: Map<String, String>
    ): ResponseDTO<ManagedAccountsDTO>

    @FormUrlEncoded
    @POST("$SELECT_USER/{$TIMESTAMP}/{$SALT_STRING}/{$TOKEN}")
    suspend fun selectUserForId(
        @Path(TIMESTAMP) timestamp: String,
        @Path(SALT_STRING) saltString: String,
        @Path(TOKEN) token: String,
        @FieldMap params: Map<String, String>
    ): ResponseDTO<SelectUserDTO>

    @FormUrlEncoded
    @POST("$GET_DATA_PER_CATEGORY/{$TIMESTAMP}/{$SALT_STRING}/{$TOKEN}")
    suspend fun fetchDataByCategory(
        @Path(TIMESTAMP) timestamp: String,
        @Path(SALT_STRING) saltString: String,
        @Path(TOKEN) token: String,
        @FieldMap params: Map<String, String>
    ): ResponseDTO<VoucherCategoriesDTO>

    @FormUrlEncoded
    @POST("$GET_AVAILABLE_VOUCHER_LIST_BY_CATEGORY/{$TIMESTAMP}/{$SALT_STRING}/{$TOKEN}")
    suspend fun fetchVouchersForCategory(
        @Path(TIMESTAMP) timestamp: String,
        @Path(SALT_STRING) saltString: String,
        @Path(TOKEN) token: String,
        @FieldMap params: Map<String, String>
    ): ResponseDTO<VouchersDTO>

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
    ): ResponseDTO<GetVoucherStatusDTO>

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
    @POST("$GET_USED_VOUCHER_LIST_BY_CATEGORY/{$TIMESTAMP}/{$SALT_STRING}/{$TOKEN}")
    suspend fun fetchUsedVouchersForCategory(
        @Path(TIMESTAMP) timestamp: String,
        @Path(SALT_STRING) saltString: String,
        @Path(TOKEN) token: String,
        @FieldMap params: Map<String, String>
    ): ResponseDTO<UsedVouchersDTO>

    @FormUrlEncoded
    @POST("$GET_INBOX_MESSAGE_LIST/{$TIMESTAMP}/{$SALT_STRING}/{$TOKEN}")
    suspend fun fetchInboxMessages(
        @Path(TIMESTAMP) timestamp: String,
        @Path(SALT_STRING) saltString: String,
        @Path(TOKEN) token: String,
        @FieldMap params: Map<String, String>
    ): ResponseDTO<MessagesDTO>

    @FormUrlEncoded
    @POST("$GET_INBOX_MESSAGE/{$TIMESTAMP}/{$SALT_STRING}/{$TOKEN}")
    suspend fun fetchInboxMessageForId(
        @Path(TIMESTAMP) timestamp: String,
        @Path(SALT_STRING) saltString: String,
        @Path(TOKEN) token: String,
        @FieldMap params: Map<String, String>
    ): ResponseDTO<SingleMessageDTO>

    @FormUrlEncoded
    @POST("$GET_OUTBOX_MESSAGE_LIST/{$TIMESTAMP}/{$SALT_STRING}/{$TOKEN}")
    suspend fun fetchOutboxMessages(
        @Path(TIMESTAMP) timestamp: String,
        @Path(SALT_STRING) saltString: String,
        @Path(TOKEN) token: String,
        @FieldMap params: Map<String, String>
    ): ResponseDTO<MessagesDTO>

    @FormUrlEncoded
    @POST("$GET_OUTBOX_MESSAGE/{$TIMESTAMP}/{$SALT_STRING}/{$TOKEN}")
    suspend fun fetchOutboxMessageForId(
        @Path(TIMESTAMP) timestamp: String,
        @Path(SALT_STRING) saltString: String,
        @Path(TOKEN) token: String,
        @FieldMap params: Map<String, String>
    ): ResponseDTO<SingleMessageDTO>

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
    ): ResponseDTO<DeleteMessageDTO>

    @FormUrlEncoded
    @POST("$DELETE_OUTBOX_MESSAGE/{$TIMESTAMP}/{$SALT_STRING}/{$TOKEN}")
    suspend fun deleteOutboxMessageForId(
        @Path(TIMESTAMP) timestamp: String,
        @Path(SALT_STRING) saltString: String,
        @Path(TOKEN) token: String,
        @FieldMap params: Map<String, String>
    ): ResponseDTO<DeleteMessageDTO>

    @FormUrlEncoded
    @POST("$GET_ALERT_LIST/{$TIMESTAMP}/{$SALT_STRING}/{$TOKEN}")
    suspend fun fetchAlertList(
        @Path(TIMESTAMP) timestamp: String,
        @Path(SALT_STRING) saltString: String,
        @Path(TOKEN) token: String,
        @FieldMap params: Map<String, String>
    ): ResponseDTO<AlertsDTO>
}