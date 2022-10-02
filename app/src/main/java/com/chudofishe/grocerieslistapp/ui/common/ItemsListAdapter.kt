package com.chudofishe.grocerieslistapp.ui.common

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.chudofishe.grocerieslistapp.databinding.ShoppingItemBinding
import com.chudofishe.grocerieslistapp.data.model.Category
import com.chudofishe.grocerieslistapp.data.model.ShoppingItem
import com.chudofishe.grocerieslistapp.databinding.ShoppingItemFavoriteBinding

class ItemsListAdapter(private val itemsType: ItemsListAdapterItemType,
                       private val listener: ItemsListAdapterActionsListener)
    : RecyclerView.Adapter<ItemsListViewHolder>() {

    var updateCategoryItemCount: (Int) -> Unit = {}
    val checkedItems = mutableListOf<ShoppingItem>()
    val items = mutableListOf<ShoppingItem>()

    @SuppressLint("NotifyDataSetChanged")
    fun setItemsList(list: List<ShoppingItem>) {
        if (items.isEmpty()) {
            items.addAll(list)
            notifyDataSetChanged()
        } else if (list.isEmpty()) {
            items.clear()
            notifyDataSetChanged()
        } else if (list.size < items.size) {
            val iterator = items.iterator()
            while(iterator.hasNext()) {
                val item = iterator.next()
                if (!list.contains(item)) {
                    notifyItemRemoved(items.indexOf(item))
                    iterator.remove()
                }
            }
        } else {
            val diff = list.filterNot { items.contains(it) }
            items.addAll(diff)
            notifyItemRangeInserted(items.size - diff.size, diff.size)
        }
        updateCategoryItemCount(items.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemsListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (itemsType) {
            ItemsListAdapterItemType.ACTIVE -> {
                ActiveListItemViewHolder(ShoppingItemBinding.inflate(inflater, parent, false), listener)
            }
            ItemsListAdapterItemType.HISTORIZED -> {
                HistoryListItemViewHolder(ShoppingItemBinding.inflate(inflater, parent, false), listener)
            }
            ItemsListAdapterItemType.FAVORITE_EDIT -> {
                FavoriteEditableListItemViewHolder(ShoppingItemFavoriteBinding.inflate(inflater, parent, false), listener)
            }
            ItemsListAdapterItemType.FAVORITE_SELECTION -> {
                FavoriteSelectionListItemViewHolder(ShoppingItemFavoriteBinding.inflate(inflater, parent, false), checkedItems, listener)
            }
        }
    }

    override fun onBindViewHolder(holder: ItemsListViewHolder, position: Int) {
        val item = items[position]
        when (itemsType) {
            ItemsListAdapterItemType.ACTIVE -> {
                (holder as ActiveListItemViewHolder).bind(item)
            }
            ItemsListAdapterItemType.HISTORIZED -> {
                (holder as HistoryListItemViewHolder).bind(item)
            }
            ItemsListAdapterItemType.FAVORITE_EDIT -> {
                (holder as FavoriteEditableListItemViewHolder).bind(item)
            }
            ItemsListAdapterItemType.FAVORITE_SELECTION -> {
                (holder as FavoriteSelectionListItemViewHolder).bind(item)
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
