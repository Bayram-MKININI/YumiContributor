package net.noliaware.yumi_contributor.feature_message.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import net.noliaware.yumi_contributor.feature_message.data.repository.MessageRepositoryImpl
import net.noliaware.yumi_contributor.feature_message.domain.repository.MessageRepository

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class MessageModule {
    @Binds
    @ActivityRetainedScoped
    abstract fun bindMessageRepository(messageRepository: MessageRepositoryImpl): MessageRepository
}