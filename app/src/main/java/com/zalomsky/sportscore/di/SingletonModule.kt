package com.zalomsky.sportscore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.zalomsky.sportscore.api.CityApi
import com.zalomsky.sportscore.api.CountryApi
import com.zalomsky.sportscore.api.LeagueApi
import com.zalomsky.sportscore.api.PlayerApi
import com.zalomsky.sportscore.api.TeamApi
import com.zalomsky.sportscore.api.UserApi
import com.zalomsky.sportscore.network.NetworkUtils.BASE_URL
import com.zalomsky.sportscore.utils.PreferenceManager
import com.zalomsky.sportscore.utils.TokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "data_store")

@Module
@InstallIn(SingletonComponent::class)
class SingletonModule {

    @Singleton
    @Provides
    fun provideTokenManager(@ApplicationContext context: Context): TokenManager =
        TokenManager(context)

    @Singleton
    @Provides
    fun providePreferenceManager(@ApplicationContext context: Context): PreferenceManager =
        PreferenceManager(context)

    @Singleton
    @Provides
    fun provideOkHttpClient(preferenceManager: PreferenceManager): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val tokenInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()

            val token = preferenceManager.getToken()

            if (token != null) {
                val newRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer $token")
                    .build()
                chain.proceed(newRequest)
            } else {
                chain.proceed(originalRequest)
            }
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(tokenInterceptor)
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Singleton
    @Provides
    fun provideUserApiService(retrofit: Retrofit): UserApi =
        retrofit.create(UserApi::class.java)

    @Singleton
    @Provides
    fun provideCountryApiService(retrofit: Retrofit): CountryApi =
        retrofit.create(CountryApi::class.java)

    @Singleton
    @Provides
    fun provideCityApiService(retrofit: Retrofit): CityApi =
        retrofit.create(CityApi::class.java)

    @Singleton
    @Provides
    fun provideLeagueApiService(retrofit: Retrofit): LeagueApi =
        retrofit.create(LeagueApi::class.java)

    @Singleton
    @Provides
    fun providePlayerApiService(retrofit: Retrofit): PlayerApi =
        retrofit.create(PlayerApi::class.java)

    @Singleton
    @Provides
    fun provideTeamApiService(retrofit: Retrofit): TeamApi =
        retrofit.create(TeamApi::class.java)
}