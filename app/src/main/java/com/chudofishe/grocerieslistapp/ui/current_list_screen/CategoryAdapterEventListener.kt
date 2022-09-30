package com.chudofishe.grocerieslistapp.ui.current_list_screen

import com.chudofishe.grocerieslistapp.data.model.ShoppingItem

interface CategoryAdapterEventListener {
    fun onItemClicked(item: ShoppingItem)
    fun onItemLongClicked(item: ShoppingItem)
    fun onRemoveButtonClicked(item: ShoppingItem)
    fun onCleared()
    fun onCompleted(list: List<ShoppingItem>)
    fun onCategoryAdded()
    fun onCategoryCleared(list: List<ShoppingItem>)
    fun onCategoryCompleted(list: List<ShoppingItem>)
}
