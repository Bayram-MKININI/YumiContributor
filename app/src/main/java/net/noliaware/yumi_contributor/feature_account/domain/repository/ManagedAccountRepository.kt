package net.noliaware.yumi_contributor.feature_account.domain.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import net.noliaware.yumi_contributor.commun.util.Resource
import net.noliaware.yumi_contributor.feature_account.domain.model.Category
import net.noliaware.yumi_contributor.feature_account.domain.model.ManagedAccount
import net.noliaware.yumi_contributor.feature_account.domain.model.Voucher
import net.noliaware.yumi_contributor.feature_account.domain.model.VoucherRequest
import net.noliaware.yumi_contributor.feature_account.domain.model.VoucherStateData

interface ManagedAccountRepository {

    fun updatePrivacyPolicyReadStatus(): Flow<Resource<Boolean>>

    fun getFilterUsers(): Flow<Resource<List<ManagedAccount>>>

    fun getManagedAccountForId(userId: String): Flow<Resource<ManagedAccount>>

    fun getManagedAccountList(): Flow<PagingData<ManagedAccount>>

    fun selectManagedAccountForId(accountId: String): Flow<Resource<String>>

    fun getAvailableCategories(): Flow<Resource<List<Category>>>

    fun getAvailableVoucherList(categoryId: String): Flow<PagingData<Voucher>>

    fun getUsedCategories(): Flow<Resource<List<Category>>>

    fun getUsedVoucherList(categoryId: String): Flow<PagingData<Voucher>>

    fun getCancelledCategories(): Flow<Resource<List<Category>>>

    fun getCancelledVoucherList(categoryId: String): Flow<PagingData<Voucher>>

    fun getVoucherById(voucherId: String): Flow<Resource<Voucher>>

    fun sendVoucherRequestWithId(
        voucherId: String,
        voucherRequestTypeId: Int,
        voucherRequestComment: String
    ): Flow<Resource<Boolean>>

    fun getVoucherRequestListById(voucherId: String): Flow<Resource<List<VoucherRequest>>>

    fun getVoucherStateDataById(voucherId: String): Flow<Resource<VoucherStateData>>

    fun useVoucherById(voucherId: String): Flow<Resource<Boolean>>
}