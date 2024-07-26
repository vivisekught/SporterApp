package com.graduate.work.sporterapp.di.local_db

import android.content.Context
import com.graduate.work.sporterapp.data.local_db.LocalDbRepositoryImpl
import com.graduate.work.sporterapp.domain.local_db.LocalDbRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
interface LocalDbModule {

    @Binds
    fun bindLocalDbRepository(localDbRepositoryImpl: LocalDbRepositoryImpl): LocalDbRepository

    companion object {
        @Provides
        @Singleton
        fun provideSharedPreferences(@ApplicationContext context: Context) =
            context.getSharedPreferences("SporterApp", Context.MODE_PRIVATE)
    }
}