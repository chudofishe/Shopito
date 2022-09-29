package com.chudofishe.grocerieslistapp.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Entity
@Serializable
@Parcelize
data class ShoppingItem(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val text: String,
    var description: String? = null,
    var currentCategory: Category,
    val originalCategory: Category
) : Parcelable