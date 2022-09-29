package com.chudofishe.grocerieslistapp.data.dao

import androidx.room.*
import com.chudofishe.grocerieslistapp.data.model.ShoppingList
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingListDao {

    @Query("SELECT * FROM shoppinglist LIMIT 1")
    fun getActiveList(): Flow<ShoppingList>

    @Query("SELECT * FROM shoppinglist ORDER BY id DESC")
    fun getAll(): Flow<List<ShoppingList>>

    @Query("SELECT * FROM shoppinglist WHERE isFavorite = :isFavorite ORDER BY id DESC")
    fun getFavorites(isFavorite: Boolean = true): Flow<List<ShoppingList>>

    @Query("SELECT * FROM shoppinglist WHERE id = :id")
    suspend fun getById(id: Long): ShoppingList?

    @Insert
    suspend fun insert(list: ShoppingList)

    @Query("DELETE FROM shoppinglist")
    suspend fun deleteAll()

    @Delete
    suspend fun delete(list: ShoppingList)

    @Update
    suspend fun update(list: ShoppingList)
}