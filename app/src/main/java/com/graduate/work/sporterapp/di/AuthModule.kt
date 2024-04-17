package com.graduate.work.data.firebase.di

import com.google.firebase.auth.FirebaseAuth
import com.graduate.work.sporterapp.domain.firebase.auth.FirebaseAuthRepository
import com.graduate.work.sporterapp.data.firebase.auth.FirebaseAuthRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface AuthModule {

    @Binds
    @Singleton
    fun bindFirebaseAuthDataRepository(impl: FirebaseAuthRepositoryImpl): FirebaseAuthRepository

    companion object {
        @Provides
        @Singleton
        fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()
    }
}