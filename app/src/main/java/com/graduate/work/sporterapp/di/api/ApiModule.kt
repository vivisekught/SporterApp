package com.graduate.work.sporterapp.di.api

import com.graduate.work.sporterapp.data.api.ElevationApiRepositoryImpl
import com.graduate.work.sporterapp.data.api.factory.RetrofitApiFactory
import com.graduate.work.sporterapp.data.api.services.ElevationService
import com.graduate.work.sporterapp.domain.api.ElevationApiRepository
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

    companion object {
        @Provides
        @Singleton
        fun provideElevationApiInstance() =
            RetrofitApiFactory().createInstance(ElevationService::class.java)
    }

}