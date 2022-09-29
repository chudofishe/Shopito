package com.chudofishe.grocerieslistapp.ui.common

import android.app.AlertDialog
import android.content.DialogInterface
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chudofishe.grocerieslistapp.R

abstract class BaseCardViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    abstract val showPopUpMenu: View.OnClickListener
    abstract val toggleCollapse: View.OnClickListener

    fun updateItemCount(textView: TextView, count: Int) {
        textView.text = count.toString()
    }

    fun showConfirmAlertDialog(questionRes: Int, onPositiveListener: DialogInterface.OnClickListener) {
        val builder = AlertDialog.Builder(this.itemView.context)
        builder.setMessage(questionRes)
            .setPositiveButton(R.string.yes, onPositiveListener)
            .setNegativeButton(R.string.cancel, null)
            .create().show()
    }
}