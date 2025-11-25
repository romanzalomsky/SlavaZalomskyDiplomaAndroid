package com.zalomsky.sportscore.di

import com.zalomsky.sportscore.api.CityApi
import com.zalomsky.sportscore.api.CountryApi
import com.zalomsky.sportscore.api.LeagueApi
import com.zalomsky.sportscore.api.PlayerApi
import com.zalomsky.sportscore.api.TeamApi
import com.zalomsky.sportscore.api.UserApi
import com.zalomsky.sportscore.data.CityRepositoryImpl
import com.zalomsky.sportscore.data.CountryRepositoryImpl
import com.zalomsky.sportscore.data.LeagueRepositoryImpl
import com.zalomsky.sportscore.data.PlayerRepositoryImpl
import com.zalomsky.sportscore.data.TeamRepositoryImpl
import com.zalomsky.sportscore.data.UserRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class HiltModule {

    @Provides
    fun provideUserRepository(userApi: UserApi): UserRepositoryImpl =
        UserRepositoryImpl(userApi)

    @Provides
    fun provideCountryRepository(countryApi: CountryApi): CountryRepositoryImpl =
        CountryRepositoryImpl(countryApi)

    @Provides
    fun provideCityRepository(cityApi: CityApi): CityRepositoryImpl =
        CityRepositoryImpl(cityApi)

    @Provides
    fun provideLeagueRepository(leagueApi: LeagueApi, teamApi: TeamApi): LeagueRepositoryImpl =
        LeagueRepositoryImpl(leagueApi, teamApi)

    @Provides
    fun providePlayerRepository(playerApi: PlayerApi): PlayerRepositoryImpl =
        PlayerRepositoryImpl(playerApi)

    @Provides
    fun provideTeamRepository(teamApi: TeamApi): TeamRepositoryImpl =
        TeamRepositoryImpl(teamApi)
}