package com.chudofishe.grocerieslistapp.di

import android.content.Context
import androidx.room.Room
import com.chudofishe.grocerieslistapp.data.AppDatabase
import com.chudofishe.grocerieslistapp.data.repository.ActiveListRepository
import com.chudofishe.grocerieslistapp.data.dao.ShoppingItemDao
import com.chudofishe.grocerieslistapp.data.dao.ShoppingListDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext applicationContext: Context): AppDatabase {
        return Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "groceriesAppDb"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideShoppingListDao(appDatabase: AppDatabase): ShoppingListDao =
        appDatabase.shoppingListDao

    @Provides
    @Singleton
    fun provideShoppingItemDao(appDatabase: AppDatabase): ShoppingItemDao =
        appDatabase.shoppingItemDao


    @Provides
    @Singleton
    fun provideActiveListRepository(@ApplicationContext applicationContext: Context): ActiveListRepository {
        return ActiveListRepository(applicationContext)
    }
}