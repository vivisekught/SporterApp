package com.graduate.work.sporterapp.di.api

import com.graduate.work.sporterapp.data.api.elevation.ElevationApiRepositoryImpl
import com.graduate.work.sporterapp.data.api.factory.RetrofitApiFactory
import com.graduate.work.sporterapp.data.api.elevation.services.ElevationService
import com.graduate.work.sporterapp.data.api.strava.StravaApiRepositoryImpl
import com.graduate.work.sporterapp.data.api.strava.service.StravaService
import com.graduate.work.sporterapp.domain.api.ElevationApiRepository
import com.graduate.work.sporterapp.domain.api.StravaApiRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface ApiModule {

    @Binds
    fun bindElevationApiRepository(elevationApiRepositoryImpl: ElevationApiRepositoryImpl): ElevationApiRepository

    @Binds
    fun bindStravaApiRepository(stravaApiRepositoryImpl: StravaApiRepositoryImpl): StravaApiRepository
    companion object {
        @Provides
        @Singleton
        fun provideElevationApiInstance() =
            RetrofitApiFactory().createInstance(ElevationService::class.java)

        @Provides
        @Singleton
        fun provideStravaApiInstance() =
            RetrofitApiFactory().createInstance(StravaService::class.java)
    }

}