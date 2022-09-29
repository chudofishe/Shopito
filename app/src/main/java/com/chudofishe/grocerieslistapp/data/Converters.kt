package com.chudofishe.grocerieslistapp.data

import androidx.room.TypeConverter
import com.chudofishe.grocerieslistapp.data.model.Category
import com.chudofishe.grocerieslistapp.data.model.ShoppingItem
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDate


class Converters {
    @TypeConverter
    fun shoppingItemsListToJson(value: List<ShoppingItem>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun shoppingItemsListFromJson(value: String): List<ShoppingItem> {
        return Json.decodeFromString(value)
    }

    @TypeConverter
    fun categoryToString(value: Category): String {
        return value.toString()
    }

    @TypeConverter
    fun categoryFromString(value: String): Category {
        return Category.valueOf(value)
    }

    @TypeConverter
    fun localDateToEpochDay(value: LocalDate): Long {
        return value.toEpochDay()
    }

    @TypeConverter
    fun localDateFromEpochDay(value: Long): LocalDate {
        return LocalDate.ofEpochDay(value)
    }
}