package net.noliaware.yumi_contributor.feature_account.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import net.noliaware.yumi_contributor.feature_account.data.repository.ManagedAccountRepositoryImpl
import net.noliaware.yumi_contributor.feature_account.domain.repository.ManagedAccountRepository

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class AccountModule {
    @Binds
    @ActivityRetainedScoped
    abstract fun bindManagedAccountRepository(alertsRepository: ManagedAccountRepositoryImpl): ManagedAccountRepository
}