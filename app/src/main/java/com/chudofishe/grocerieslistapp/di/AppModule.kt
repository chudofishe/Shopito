package com.chudofishe.grocerieslistapp.di

import android.content.Context
import com.chudofishe.grocerieslistapp.data.SharedPrefAppStore
import com.chudofishe.grocerieslistapp.data.SharedPrefDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSharedPrefAppStore(@ApplicationContext applicationContext: Context): SharedPrefAppStore {
        return SharedPrefAppStore(applicationContext)
    }
}