package net.noliaware.yumi_contributor.feature_login.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import net.noliaware.yumi_contributor.feature_login.data.repository.LoginRepositoryImpl
import net.noliaware.yumi_contributor.feature_login.domain.repository.LoginRepository

@Module
@InstallIn(ViewModelComponent::class)
abstract class LoginModule {
    @Binds
    @ViewModelScoped
    abstract fun bindLoginRepository(loginRepository: LoginRepositoryImpl): LoginRepository
}