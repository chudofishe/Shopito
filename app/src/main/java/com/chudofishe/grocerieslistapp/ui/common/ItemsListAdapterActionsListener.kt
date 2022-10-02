package com.chudofishe.grocerieslistapp.ui.common

import com.chudofishe.grocerieslistapp.data.model.ShoppingItem

interface ItemsListAdapterActionsListener {
    fun onItemClicked(item: ShoppingItem) {}
    fun onItemLongClicked(item: ShoppingItem) {}
    fun onRemoveButtonClicked(item: ShoppingItem) {}
    fun onUpdateButtonClicked(item: ShoppingItem) {}
}