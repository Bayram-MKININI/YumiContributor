package net.noliaware.yumi_contributor.feature_profile.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import net.noliaware.yumi_contributor.feature_profile.data.repository.ProfileRepositoryImpl
import net.noliaware.yumi_contributor.feature_profile.domain.repository.ProfileRepository

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class ProfileModule {
    @Binds
    @ActivityRetainedScoped
    abstract fun bindProfileRepository(profileRepository: ProfileRepositoryImpl): ProfileRepository
}