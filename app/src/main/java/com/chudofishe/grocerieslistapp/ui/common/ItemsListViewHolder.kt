package com.chudofishe.grocerieslistapp.ui.common

import android.app.AlertDialog
import android.view.View
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.chudofishe.grocerieslistapp.R
import com.chudofishe.grocerieslistapp.data.model.Category
import com.chudofishe.grocerieslistapp.data.model.ShoppingItem
import com.chudofishe.grocerieslistapp.databinding.ShoppingItemBinding
import com.chudofishe.grocerieslistapp.databinding.ShoppingItemFavoriteBinding

enum class ItemsListAdapterItemType {
    ACTIVE, HISTORIZED, FAVORITE_EDIT, FAVORITE_SELECTION
}

abstract class ItemsListViewHolder(view: View,
                                   private val listener: ItemsListAdapterActionsListener) : RecyclerView.ViewHolder(view) {

    abstract fun bind(item: ShoppingItem)
    abstract fun setLeadingIconImage(category: Category)

    fun showSaveItemDialog(item: ShoppingItem) {
        AlertDialog.Builder(itemView.context)
            .setMessage(itemView.context.getString(R.string.question_save_to_favorites))
            .setPositiveButton(R.string.yes) { _, _ ->
                listener.onItemLongClicked(item)
            }
            .setNegativeButton(R.string.no, null)
            .create().show()
    }
}

class ActiveListItemViewHolder(private val binding: ShoppingItemBinding,
                               private val listener: ItemsListAdapterActionsListener)
    : ItemsListViewHolder(binding.root, listener) {

    override fun bind(item: ShoppingItem) {
        with(binding) {
            text.text = item.text
            item.description?.let {
                properties.visibility = View.VISIBLE
                properties.text = it
            }
            remove.setOnClickListener {
                listener.onRemoveButtonClicked(item)
            }
            root.setOnClickListener {
                listener.onItemClicked(item)
            }
            root.setOnLongClickListener {
                showSaveItemDialog(item)
                true
            }
        }
        setLeadingIconImage(item.currentCategory)
    }

    override fun setLeadingIconImage(category: Category) {
        with(binding) {
            if (category == Category.DONE) {
                dotImage.visibility = View.GONE
            } else {
                checkImage.visibility = View.GONE
            }
        }
    }
}

class HistoryListItemViewHolder(private val binding: ShoppingItemBinding,
                                listener: ItemsListAdapterActionsListener)
    : ItemsListViewHolder(binding.root, listener) {

    override fun bind(item: ShoppingItem) {
        with(binding) {
            text.text = item.text
            item.description?.let {
                properties.visibility = View.VISIBLE
                properties.text = it
            }
            if (item.originalCategory != Category.UNCATEGORIZED) {
                categoryIcon.apply {
                    visibility = View.VISIBLE
                    setImageResource(item.originalCategory.drawable)
                }
            } else {
                categoryIcon.visibility = View.GONE
            }
            item.description?.let {
                properties.visibility = View.VISIBLE
                properties.text = it
            }
            remove.visibility = View.GONE

            root.setOnLongClickListener {
                showSaveItemDialog(item)
                true
            }
        }
        setLeadingIconImage(item.currentCategory)
    }

    override fun setLeadingIconImage(category: Category) {
        with(binding) {
            if (category == Category.DONE) {
                dotImage.visibility = View.GONE
            } else {
                checkImage.visibility = View.GONE
            }
        }
    }
}

class FavoriteEditableListItemViewHolder(private val binding: ShoppingItemFavoriteBinding,
                                         private val listener: ItemsListAdapterActionsListener)
    : ItemsListViewHolder(binding.root, listener) {

    override fun bind(item: ShoppingItem) {
        with(binding) {
            text.text = item.text
            item.description?.let {
                properties.visibility = View.VISIBLE
                properties.text = it
            }
            binding.options.setOnClickListener {
                showPopUpMenu(item)
            }
            binding.checkBox.visibility = View.GONE
        }
        setLeadingIconImage(item.originalCategory)
    }

    private fun showPopUpMenu(item: ShoppingItem) {
        PopupMenu(itemView.context, binding.options).apply {
            inflate(R.menu.shopping_item_favorite_menu)
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.shopping_item_favorite_menu_delete -> {
                        listener.onRemoveButtonClicked(item)
                    }
                    R.id.shopping_item_favorite_menu_edit -> {
                        listener.onUpdateButtonClicked(item)
                    }
                }
                true
            }
        }.show()
    }

    override fun setLeadingIconImage(category: Category) {
        with(binding) {
            if (category == Category.UNCATEGORIZED) {
                categoryIcon.visibility = View.INVISIBLE
            } else {
                categoryIcon.visibility = View.VISIBLE
                categoryIcon.setImageResource(category.drawable)
            }
        }
    }
}

class FavoriteSelectionListItemViewHolder(private val binding: ShoppingItemFavoriteBinding,
                                          private val checkedItems: MutableList<ShoppingItem>,
                                          listener: ItemsListAdapterActionsListener)
    : ItemsListViewHolder(binding.root, listener) {

    private var checked = false

    override fun bind(item: ShoppingItem) {
        with(binding) {
            text.text = item.text
            item.description?.let {
                properties.visibility = View.VISIBLE
                properties.text = it
            }
            binding.options.visibility = View.GONE
            binding.checkBox.setOnClickListener {
                if (!checked)
                    checkedItems.add(item)
                else
                    checkedItems.remove(item)
                checked = !checked
            }
        }
        setLeadingIconImage(item.originalCategory)
    }

    override fun setLeadingIconImage(category: Category) {
        with(binding) {
            if (category == Category.UNCATEGORIZED) {
                categoryIcon.visibility = View.INVISIBLE
            } else {
                categoryIcon.visibility = View.VISIBLE
                categoryIcon.setImageResource(category.drawable)
            }
        }
    }
}

