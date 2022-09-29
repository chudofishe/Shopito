package com.chudofishe.grocerieslistapp.ui.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.chudofishe.grocerieslistapp.databinding.ShoppingItemBinding
import com.chudofishe.grocerieslistapp.data.model.Category
import com.chudofishe.grocerieslistapp.data.model.ShoppingItem
import com.chudofishe.grocerieslistapp.databinding.ShoppingItemFavoriteBinding

class ItemsListAdapter(private val itemsType: ItemsListAdapterItemType,
                       val listener: ItemsListAdapterActionsListener? = null
) : ListAdapter<ShoppingItem, ItemsListViewHolder>(ShoppingItemDiffCallback()) {

    var updateCategoryItemCount: (Int) -> Unit = {}
    val checkedItems = mutableListOf<ShoppingItem>()
    val items = mutableListOf<ShoppingItem>()
    var listCategory: Category? = null

    class ShoppingItemDiffCallback : DiffUtil.ItemCallback<ShoppingItem>() {
        override fun areItemsTheSame(oldItem: ShoppingItem, newItem: ShoppingItem): Boolean {
            return oldItem.text == newItem.text &&
                    oldItem.originalCategory == newItem.originalCategory
        }

        override fun areContentsTheSame(oldItem: ShoppingItem, newItem: ShoppingItem): Boolean {
            return areItemsTheSame(oldItem, newItem) &&
                    oldItem.currentCategory == newItem.currentCategory &&
                    oldItem.description == newItem.description
        }

    }

    fun remove(pos: Int) {
        val list = currentList.toMutableList()
        list.removeAt(pos)
        items.removeAt(pos)
        submitList(list)
    }

    fun add(newItem: ShoppingItem) {
        if (itemsType == ItemsListAdapterItemType.ACTIVE) {
            listCategory = newItem.currentCategory
        }
        val list = currentList.toMutableList()
        list.add(newItem)
        items.add(newItem)
        submitList(list)
    }

    fun addList(list: List<ShoppingItem>) {
        val newList = currentList.toMutableList()
        newList.addAll(list)
        items.addAll(list)
        submitList(newList)
    }

    override fun onCurrentListChanged(
        previousList: MutableList<ShoppingItem>,
        currentList: MutableList<ShoppingItem>
    ) {
        if (currentList.isEmpty() && previousList.isNotEmpty()) {
            listCategory?.let {
                listener?.removeCategory(it)
            }
        } else {
            updateCategoryItemCount(currentList.size)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemsListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (itemsType) {
            ItemsListAdapterItemType.ACTIVE -> {
                ActiveListItemViewHolder(ShoppingItemBinding.inflate(inflater, parent, false), this)
            }
            ItemsListAdapterItemType.HISTORIZED -> {
                HistoryListItemViewHolder(ShoppingItemBinding.inflate(inflater, parent, false), this)
            }
            ItemsListAdapterItemType.FAVORITE_EDIT -> {
                FavoriteEditableListItemViewHolder(ShoppingItemFavoriteBinding.inflate(inflater, parent, false), this)
            }
            ItemsListAdapterItemType.FAVORITE_SELECTION -> {
                FavoriteSelectionListItemViewHolder(ShoppingItemFavoriteBinding.inflate(inflater, parent, false), this)
            }
        }
    }

    override fun onBindViewHolder(holder: ItemsListViewHolder, position: Int) {
        val item = getItem(position)
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
}

interface ItemsListAdapterActionsListener {
    fun onItemClicked(item: ShoppingItem) {}
    fun removeCategory(category: Category?) {}
    fun updateItem(item: ShoppingItem) {}
    fun deleteItem(item: ShoppingItem) {}
    fun saveItem(item: ShoppingItem) {}
}