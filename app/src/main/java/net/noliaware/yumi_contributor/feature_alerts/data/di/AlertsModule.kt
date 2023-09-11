package net.noliaware.yumi_contributor.feature_alerts.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import net.noliaware.yumi_contributor.feature_alerts.data.repository.AlertsRepositoryImpl
import net.noliaware.yumi_contributor.feature_alerts.domain.repository.AlertsRepository

@Module
@InstallIn(ViewModelComponent::class)
abstract class AlertsModule {
    @Binds
    @ViewModelScoped
    abstract fun bindAlertsRepository(alertsRepository: AlertsRepositoryImpl): AlertsRepository
}