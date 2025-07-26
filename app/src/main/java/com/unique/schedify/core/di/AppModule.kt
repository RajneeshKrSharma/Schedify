package com.unique.schedify.core.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.unique.schedify.BuildConfig
import com.unique.schedify.auth.login.data.remote.LoginApis
import com.unique.schedify.post_auth.split_expense.data.remote.dto.SplitExpenseApis
import com.unique.schedify.core.ConnectivityChecker
import com.unique.schedify.core.PendingRequestManager
import com.unique.schedify.core.config.SharedPrefConfig
import com.unique.schedify.core.local_db.SchedifyDatabase
import com.unique.schedify.core.network.Api
import com.unique.schedify.post_auth.address.data.remote.dto.AddressApis
import com.unique.schedify.post_auth.post_auth_loading.data.remote.PostAuthApis
import com.unique.schedify.post_auth.post_auth_loading.local.UserMappedWeatherStatusDao
import com.unique.schedify.post_auth.schedule_list.remote.ScheduleListApis
import com.unique.schedify.pre_auth.pre_auth_loading.data.local.PreAuthDao
import com.unique.schedify.pre_auth.pre_auth_loading.data.remote.PreAuthApis
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(sharedPrefConfig: SharedPrefConfig): Retrofit {
        val clientBuilder = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val requestWithHeaders = originalRequest.newBuilder()
                    .apply {
                        addHeader("Content-Type", "application/json")
                        addHeader("Accept", "application/json")
                        sharedPrefConfig.getAuthToken()?.let { token ->
                            if (sharedPrefConfig.isUserViaOtp())
                                addHeader("Authorization", token)
                            else
                                addHeader("Authorization", "Bearer $token")
                        }
                    }
                    .build()
                chain.proceed(requestWithHeaders)
            }

        if (BuildConfig.DEBUG) {
            clientBuilder.addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
        }

        return Retrofit.Builder()
            .baseUrl(Api.BASE_URL)
            // .addConverterFactory(EncryptedRequestConverterFactory(Gson())) // if using encryption
            .addConverterFactory(GsonConverterFactory.create())
            .client(clientBuilder.build())
            .build()
    }


    @Provides
    @Singleton
    fun providePreAuthApi(retrofit: Retrofit): PreAuthApis {
        return retrofit.create(PreAuthApis::class.java)
    }

    @Provides
    @Singleton
    fun provideLoginApi(retrofit: Retrofit): LoginApis {
        return retrofit.create(LoginApis::class.java)
    }

    @Provides
    @Singleton
    fun provideSplitExpenseApi(retrofit: Retrofit): SplitExpenseApis {
        return retrofit.create(SplitExpenseApis::class.java)
    }

    @Provides
    @Singleton
    fun provideAddressApi(retrofit: Retrofit): AddressApis {
        return retrofit.create(AddressApis::class.java)
    }

    @Provides
    @Singleton
    fun provideSchedifyDatabase(@ApplicationContext context: Context): SchedifyDatabase {
        return Room.databaseBuilder(
            context,
            SchedifyDatabase::class.java,
            "SchedifyDb.db"
        ).build()
    }

    @Provides
    @Singleton
    fun providePendingRequestManager(connectivityChecker: ConnectivityChecker): PendingRequestManager {
        return PendingRequestManager(connectivityChecker)
    }

    @Module
    @InstallIn(SingletonComponent::class)
    object SharedPreferencesModule {

        @Provides
        @Singleton
        fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
            return context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        }

        @Provides
        @Singleton
        fun provideSharedPrefConfig(sharedPreferences: SharedPreferences): SharedPrefConfig {
            return SharedPrefConfig(sharedPreferences)
        }
    }

    @Provides
    @Singleton
    fun providePostAuthApi(retrofit: Retrofit): PostAuthApis {
        return retrofit.create(PostAuthApis::class.java)
    }

    @Provides
    fun providePreAuthDao(database: SchedifyDatabase): PreAuthDao {
        return database.preAuthDao
    }

    @Provides
    fun provideUserMappedWeatherStatusDto(database: SchedifyDatabase): UserMappedWeatherStatusDao {
        return database.userMappedWeatherStatusDao
    }

    @Provides
    @Singleton
    fun provideScheduleListApis( retrofit: Retrofit ): ScheduleListApis {
        return retrofit.create(ScheduleListApis::class.java)
    }
}