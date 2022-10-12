package com.chudofishe.grocerieslistapp.ui.active_list_screen

import com.chudofishe.grocerieslistapp.data.model.Category
import com.chudofishe.grocerieslistapp.data.model.ShoppingItem

interface CategoryAdapterEventListener {
    fun onItemClicked(item: ShoppingItem)
    fun onItemLongClicked(item: ShoppingItem)
    fun onRemoveButtonClicked(item: ShoppingItem)
    fun onCategoryCleared(list: List<ShoppingItem>)
    fun onCategoryCompleted(list: List<ShoppingItem>)
    fun onAddButtonClicked(category: Category)
}
