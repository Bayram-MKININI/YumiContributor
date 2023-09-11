package net.noliaware.yumi_contributor.commun.di

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.noliaware.yumi_contributor.BuildConfig
import net.noliaware.yumi_contributor.commun.ApiConstants.BASE_ENDPOINT
import net.noliaware.yumi_contributor.commun.data.remote.RemoteApi
import net.noliaware.yumi_contributor.commun.domain.model.SessionData
import net.noliaware.yumi_contributor.feature_account.data.repository.ManagedAccountRepositoryImpl
import net.noliaware.yumi_contributor.feature_account.domain.repository.ManagedAccountRepository
import net.noliaware.yumi_contributor.feature_alerts.data.repository.AlertsRepositoryImpl
import net.noliaware.yumi_contributor.feature_alerts.domain.repository.AlertsRepository
import net.noliaware.yumi_contributor.feature_login.data.repository.DataStoreRepositoryImpl
import net.noliaware.yumi_contributor.feature_login.data.repository.LoginRepositoryImpl
import net.noliaware.yumi_contributor.feature_login.domain.repository.DataStoreRepository
import net.noliaware.yumi_contributor.feature_login.domain.repository.LoginRepository
import net.noliaware.yumi_contributor.feature_message.data.repository.MessageRepositoryImpl
import net.noliaware.yumi_contributor.feature_message.domain.repository.MessageRepository
import net.noliaware.yumi_contributor.feature_profile.data.repository.ProfileRepositoryImpl
import net.noliaware.yumi_contributor.feature_profile.domain.repository.ProfileRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSessionData() = SessionData()

    @Provides
    fun provideOkHttpClient() = if (BuildConfig.DEBUG) {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .followRedirects(true)
            .build()
    } else OkHttpClient
        .Builder()
        .followRedirects(true)
        .build()

    @Provides
    fun provideRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit.Builder = Retrofit.Builder()
        .baseUrl(BASE_ENDPOINT)
        .client(okHttpClient)
        .addConverterFactory(
            MoshiConverterFactory.create(
                Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
            )
        )

    @Provides
    @Singleton
    fun provideDataStoreRepository(@ApplicationContext context: Context): DataStoreRepository {
        return DataStoreRepositoryImpl(context)
    }

    @Provides
    @Singleton
    fun provideApi(builder: Retrofit.Builder): RemoteApi = builder.build().create(RemoteApi::class.java)

    @Provides
    fun provideLoginRepository(api: RemoteApi, sessionData: SessionData): LoginRepository {
        return LoginRepositoryImpl(api, sessionData)
    }

    @Provides
    fun provideAccountRepository(api: RemoteApi, sessionData: SessionData): ManagedAccountRepository {
        return ManagedAccountRepositoryImpl(api, sessionData)
    }

    @Provides
    fun provideProfileRepository(api: RemoteApi, sessionData: SessionData): ProfileRepository {
        return ProfileRepositoryImpl(api, sessionData)
    }

    @Provides
    fun provideMessageRepository(api: RemoteApi, sessionData: SessionData): MessageRepository {
        return MessageRepositoryImpl(api, sessionData)
    }

    @Provides
    fun provideAlertsRepository(api: RemoteApi, sessionData: SessionData): AlertsRepository {
        return AlertsRepositoryImpl(api, sessionData)
    }
}