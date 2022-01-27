package com.makazemi.bitcoinwidget.di


import android.content.Context
import com.makazemi.bitcoinwidget.network.ApiResponseCallAdapterFactory
import com.makazemi.bitcoinwidget.network.CheckConnection
import com.makazemi.bitcoinwidget.network.MainApiService
import com.makazemi.bitcoinwidget.repository.MainRepository
import com.makazemi.bitcoinwidget.repository.MainRepositoryImp
import com.makazemi.bitcoinwidget.util.Constant.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Singleton
    @Provides
    fun provideRetrofitBuilder(): Retrofit {
        return Retrofit.Builder()
            .client(OkHttpClient.Builder().build())
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(ApiResponseCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideMainApiService(retrofit: Retrofit): MainApiService {
        return retrofit
            .create(MainApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideCheckConnection(@ApplicationContext application: Context): CheckConnection {
        return CheckConnection(application)
    }

    @Singleton
    @Provides
    fun provideMainRepository(
        mainApiService: MainApiService,
        checkConnection: CheckConnection
    ): MainRepository {
        return MainRepositoryImp(mainApiService, checkConnection)
    }
}