package com.graduate.work.sporterapp.di.maps

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.graduate.work.sporterapp.R
import com.graduate.work.sporterapp.data.maps.location.UserLocationRepositoryImpl
import com.graduate.work.sporterapp.data.maps.mapbox.directions.MapboxDirectionsRepositoryImpl
import com.graduate.work.sporterapp.di.maps.annotations.MapboxPublicToken
import com.graduate.work.sporterapp.domain.maps.location.UserLocationRepository
import com.graduate.work.sporterapp.domain.maps.mapbox.MapboxDirectionsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface MapsModule {

    @Binds
    fun bindUserLocationRepository(impl: UserLocationRepositoryImpl): UserLocationRepository

    @Binds
    fun bindMapboxDirectionsRepository(impl: MapboxDirectionsRepositoryImpl): MapboxDirectionsRepository

    companion object {
        @Provides
        fun provideFusedLocationProviderClient(@ApplicationContext context: Context): FusedLocationProviderClient {
            return LocationServices.getFusedLocationProviderClient(context)
        }

        @Provides
        @MapboxPublicToken
        fun provideMapboxPublicToken(
            @ApplicationContext context: Context,
        ) = context.getString(R.string.mapbox_access_token)
    }
}