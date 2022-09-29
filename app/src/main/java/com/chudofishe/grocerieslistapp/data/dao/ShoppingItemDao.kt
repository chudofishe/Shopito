package com.chudofishe.grocerieslistapp.data.dao

import androidx.room.*
import com.chudofishe.grocerieslistapp.data.model.ShoppingItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingItemDao {

    @Query("SELECT * FROM shoppingitem ORDER BY id DESC")
    fun getAll(): Flow<List<ShoppingItem>>

    @Delete
    suspend fun delete(item: ShoppingItem)

    @Update
    suspend fun update(item: ShoppingItem)

    @Insert
    suspend fun insert(item: ShoppingItem)
}