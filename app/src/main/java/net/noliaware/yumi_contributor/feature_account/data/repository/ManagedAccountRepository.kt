package net.noliaware.yumi_contributor.feature_account.data.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import net.noliaware.yumi_contributor.commun.util.Resource
import net.noliaware.yumi_contributor.feature_account.domain.model.Category
import net.noliaware.yumi_contributor.feature_account.domain.model.ManagedAccount
import net.noliaware.yumi_contributor.feature_account.domain.model.Voucher
import net.noliaware.yumi_contributor.feature_account.domain.model.VoucherStatus

interface ManagedAccountRepository {

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

    fun getVoucherStatusById(voucherId: String): Flow<Resource<VoucherStatus>>

    fun useVoucherById(voucherId: String): Flow<Resource<Boolean>>
}