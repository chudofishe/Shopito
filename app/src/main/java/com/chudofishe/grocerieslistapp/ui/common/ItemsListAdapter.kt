package com.chudofishe.grocerieslistapp.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.chudofishe.grocerieslistapp.databinding.ShoppingItemBinding
import com.chudofishe.grocerieslistapp.data.model.Category
import com.chudofishe.grocerieslistapp.data.model.ShoppingItem
import com.chudofishe.grocerieslistapp.databinding.ShoppingItemFavoriteBinding

class ItemsListAdapter(private val itemsType: ItemsListAdapterItemType,
                       private val listener: ItemsListAdapterActionsListener)
    : ListAdapter<ShoppingItem, ItemsListViewHolder>(ShoppingItemDiffCallback()) {

    var updateCategoryItemCount: (Int) -> Unit = {}
    val checkedItems = mutableListOf<ShoppingItem>()
    val items = mutableListOf<ShoppingItem>()
    var listCategory: Category? = null

    class ShoppingItemDiffCallback : DiffUtil.ItemCallback<ShoppingItem>() {
        override fun areItemsTheSame(oldItem: ShoppingItem, newItem: ShoppingItem): Boolean {
            return oldItem.text == newItem.text
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
//        if (currentList.isEmpty() && previousList.isNotEmpty()) {
//            listCategory?.let {
//                listener?.removeCategory(it)
//            }
//        } else {
//            updateCategoryItemCount(currentList.size)
//        }
        updateCategoryItemCount(currentList.size)
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
                FavoriteSelectionListItemViewHolder(ShoppingItemFavoriteBinding.inflate(inflater, parent, false), listener)
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
    fun onItemLongClicked(item: ShoppingItem) {}
    fun onRemoveButtonClicked(item: ShoppingItem) {}
    fun onUpdateButtonClicked(item: ShoppingItem) {}
    fun onItemChecked(item: ShoppingItem) {}
}