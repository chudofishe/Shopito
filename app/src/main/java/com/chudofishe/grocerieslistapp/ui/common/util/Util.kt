package com.chudofishe.grocerieslistapp.ui.common.util

import android.animation.Animator
import android.content.Context
import android.text.Editable
import android.util.TypedValue
import androidx.core.content.ContextCompat
import com.chudofishe.grocerieslistapp.R
import com.chudofishe.grocerieslistapp.data.model.Category
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import java.time.LocalDate
import java.time.format.DateTimeFormatter

interface AnimatorListener : Animator.AnimatorListener {
    override fun onAnimationStart(animation: Animator?) {}

    override fun onAnimationEnd(animation: Animator?) {}

    override fun onAnimationCancel(animation: Animator?) {}

    override fun onAnimationRepeat(animation: Animator?) {}
}

fun getColorControlNormalColorId(context: Context): Int {
    val typedValue = TypedValue()
    context.theme.resolveAttribute(
        com.google.android.material.R.attr.colorControlNormal, typedValue, true)
    return typedValue.resourceId
}

fun getDaysPast(date: LocalDate): Int {
    return (LocalDate.now().toEpochDay() - date.toEpochDay()).toInt()
}

fun formatDateToDays(context: Context, date: LocalDate): String {
    return when (val days = getDaysPast(date)) {
        0 -> context.getString(R.string.today)
        1 -> context.getString(R.string.yesterday)
        in 2..7 -> context.getString(R.string.n_days_ago, days)
        else -> date.format(DateTimeFormatter.ISO_LOCAL_DATE)
    }
}

fun Editable?.toStringOrNull(): String? {
    return if (this.isNullOrEmpty()) null else this.toString()
}

fun ChipGroup.init() {
    Category.values().filterNot { it == Category.DONE || it == Category.UNCATEGORIZED }
        .forEach { category ->
            val chip = Chip(this.context).apply {
                id = category.ordinal
                isCheckable = true
                chipIcon = ContextCompat.getDrawable(this.context, category.drawable)
                text = resources.getString(category.text)
            }
            this.addView(chip)
        }
}