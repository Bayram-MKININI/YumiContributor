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
import net.noliaware.yumi_contributor.commun.data.repository.ActionsRepositoryImpl
import net.noliaware.yumi_contributor.commun.domain.model.SessionData
import net.noliaware.yumi_contributor.commun.domain.repository.ActionsRepository
import net.noliaware.yumi_contributor.commun.util.ActionParamAdapter
import net.noliaware.yumi_contributor.feature_login.data.repository.DataStoreRepositoryImpl
import net.noliaware.yumi_contributor.feature_login.domain.repository.DataStoreRepository
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
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            )
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
                Moshi.Builder()
                    .add(ActionParamAdapter())
                    .addLast(KotlinJsonAdapterFactory())
                    .build()
            )
        )

    @Provides
    @Singleton
    fun provideApi(
        builder: Retrofit.Builder
    ): RemoteApi = builder.build().create(RemoteApi::class.java)

    @Provides
    @Singleton
    fun provideDataStoreRepository(
        @ApplicationContext context: Context
    ): DataStoreRepository {
        return DataStoreRepositoryImpl(context)
    }

    @Provides
    @Singleton
    fun provideActionsRepository(
        api: RemoteApi,
        sessionData: SessionData,
        dataStoreRepository: DataStoreRepository
    ): ActionsRepository {
        return ActionsRepositoryImpl(api, sessionData, dataStoreRepository)
    }
}