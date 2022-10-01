package com.chudofishe.grocerieslistapp.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDate

@Entity
@Serializable
@Parcelize
data class ShoppingList(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,
    var title: String? = null,
    var isFavorite: Boolean = false,
    var items: List<ShoppingItem> = emptyList(),
    @Serializable(with = LocalDateSerializer::class)
    var date: LocalDate = LocalDate.now(),
) : Parcelable {

    fun resetDoneItems() {
        this.items.forEach { item ->
            if (item.currentCategory == Category.DONE) item.currentCategory = item.originalCategory
        }
    }

    fun isCompleted(): Boolean {
        this.items.forEach { if (it.currentCategory != Category.DONE) return false }
        return true
    }
}

object LocalDateSerializer : KSerializer<LocalDate> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("local_date_epoch", PrimitiveKind.LONG)

    override fun deserialize(decoder: Decoder): LocalDate {
        return LocalDate.ofEpochDay(decoder.decodeLong())
    }

    override fun serialize(encoder: Encoder, value: LocalDate) {
        encoder.encodeLong(value.toEpochDay())
    }
}
