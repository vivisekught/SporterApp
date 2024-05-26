package com.graduate.work.sporterapp.di.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.graduate.work.sporterapp.data.firebase.storage.route.CloudStorageRouteRepositoryImpl
import com.graduate.work.sporterapp.data.firebase.storage.route.ImageFirebaseStoreRepositoryImpl
import com.graduate.work.sporterapp.data.firebase.storage.workout.CloudStorageWorkoutRepositoryImpl
import com.graduate.work.sporterapp.domain.firebase.storage.routes.CloudStorageRouteRepository
import com.graduate.work.sporterapp.domain.firebase.storage.routes.ImageFirebaseStoreRepository
import com.graduate.work.sporterapp.domain.firebase.storage.workout.CloudStorageWorkoutRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface StorageModule {

    @Binds
    fun bindFirestoreRepository(impl: CloudStorageRouteRepositoryImpl): CloudStorageRouteRepository

    @Binds
    fun bindImgStorageRepository(impl: ImageFirebaseStoreRepositoryImpl): ImageFirebaseStoreRepository

    @Binds
    fun bindFirestoreWorkoutRepository(impl: CloudStorageWorkoutRepositoryImpl): CloudStorageWorkoutRepository

    companion object {
        @Provides
        fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

        @Provides
        fun provideImgStorage(): FirebaseStorage = FirebaseStorage.getInstance()
    }
}