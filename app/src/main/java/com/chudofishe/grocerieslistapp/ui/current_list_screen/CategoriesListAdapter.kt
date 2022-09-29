package com.chudofishe.grocerieslistapp.ui.current_list_screen

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.chudofishe.grocerieslistapp.R
import com.chudofishe.grocerieslistapp.databinding.CategoryCardBinding
import com.chudofishe.grocerieslistapp.data.model.Category
import com.chudofishe.grocerieslistapp.data.model.ShoppingItem
import com.chudofishe.grocerieslistapp.ui.common.*

class CategoriesListAdapter(private val categories: MutableList<Category> = mutableListOf(),
                            private val onEventListener: CategoryAdapterEventListener)
    : RecyclerView.Adapter<CategoriesListAdapter.ViewHolder>() {

    private val categoryAdapters: MutableMap<Category, ItemsListAdapter> = mutableMapOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CategoryCardBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = categories[position]
        holder.bind(category, categoryAdapters[category]!!)
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    fun addItem(item: ShoppingItem) {
        if (!categories.contains(item.currentCategory)) {
            add(item.currentCategory)
        }
        categoryAdapters[item.currentCategory]!!.add(item)
    }

    fun getItems(): List<ShoppingItem> {
        val items = mutableListOf<ShoppingItem>()
        categoryAdapters.forEach {
            items.addAll(it.value.items)
        }
        return items
    }

    fun addList(list: List<ShoppingItem>) {
        Category.values().forEach { category ->
            val filteredList = list.filter { x -> x.originalCategory ==  category }
            if (filteredList.isNotEmpty()) {
                addListToCategory(filteredList, category)
            }
        }
    }

    private fun addListToCategory(list: List<ShoppingItem>, category: Category) {
        if (!categories.contains(category)) {
            add(category)
        }
        categoryAdapters[category]!!.addList(list)
    }

    private fun add(newItem: Category) {
        when(newItem) {
            Category.UNCATEGORIZED -> {
                categories.add(0, newItem)
                notifyItemInserted(0)
            }
            Category.DONE -> {
                categories.add(newItem)
                notifyItemInserted(categories.size - 1)
            }
            else -> {
                if (categories.isEmpty()) {
                    categories.add(newItem)
                    notifyItemInserted(categories.size - 1)
                } else if (!categories.contains(Category.UNCATEGORIZED)) {
                    categories.add(0, newItem)
                    notifyItemInserted(0)
                } else {
                    categories.add(1, newItem)
                    notifyItemInserted(1)
                }
            }
        }
        categoryAdapters[newItem] =
            ItemsListAdapter(ItemsListAdapterItemType.ACTIVE, createItemsListAdapterActionsListener())
        onEventListener.onCategoryAdded()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun remove(category: Category) {
        val index = categories.indexOf(category)
        categories.remove(category)
        notifyItemRemoved(index)
        categoryAdapters.remove(category)
        if (categories.size == 0) {
            onEventListener.onCategoriesListCleared()
        }
        if (categories.size == 1 && categories[0] == Category.DONE) {
            onEventListener.onCategoriesListCompleted(getItems())
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeAll() {
        categories.clear()
        categoryAdapters.clear()
        notifyDataSetChanged()
        onEventListener.onCategoriesListCleared()
    }

//    @SuppressLint("NotifyDataSetChanged")
//    fun completeList() {
//        if (categories.isNotEmpty()) {
//            val items = mutableListOf<ShoppingItem>()
//            categoryAdapters.forEach {
//                items.addAll(it.value.items)
//            }
//            categories.clear()
//            categoryAdapters.clear()
//            notifyDataSetChanged()
//            onCategoryChangedListener.onListCompleted(if (items.isEmpty()) null else items)
//        }
//    }

    private fun createItemsListAdapterActionsListener(): ItemsListAdapterActionsListener {
        return object : ItemsListAdapterActionsListener {
            override fun onItemClicked(item: ShoppingItem) {
                if (categoryAdapters[item.currentCategory]?.items?.size == 1) {
                    remove(item.currentCategory)
                }
                onEventListener.onItemClicked(item)
            }

            override fun onItemLongClicked(item: ShoppingItem) {
                onEventListener.onItemLongClicked(item)
            }

            override fun onRemoveButtonClicked(item: ShoppingItem) {
                if (categoryAdapters[item.currentCategory]?.items?.size == 1) {
                    remove(item.currentCategory)
                }
                onEventListener.onRemoveButtonClicked(item)
            }
        }
    }

//    private fun moveItem(item: ShoppingItem) {
//        item.currentCategory =
//            if (item.currentCategory == Category.DONE) item.originalCategory else Category.DONE
//        if (categories.isNotEmpty()) {
//            addItem(item)
//        }
//    }
//
//    private fun moveItemsList(list: List<ShoppingItem>, currentCategory: Category) {
//        if (currentCategory == Category.DONE) {
//            list.forEach {
//                it.currentCategory = it.originalCategory
//            }
//            addList(list)
//        } else {
//            list.forEach {
//                it.currentCategory = Category.DONE
//            }
//            addListToCategory(list, Category.DONE)
//        }
//    }

    inner class ViewHolder(private val binding: CategoryCardBinding) : BaseCardViewHolder(binding.root) {

        private lateinit var adapter: ItemsListAdapter
        private lateinit var category: Category

        override val toggleCollapse = View.OnClickListener {
            toggleCollapse()
        }

        override val showPopUpMenu = View.OnClickListener {
            PopupMenu(itemView.context, binding.options).apply {
                inflate(
                    if (category == Category.DONE)
                        R.menu.category_done_card_menu
                    else
                        R.menu.category_card_menu
                )
                setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.menu_delete_all -> {
                            onEventListener.onCategoryCleared(adapter.currentList)
                            remove(category)
                        }
                        R.id.menu_complete_all, R.id.menu_uncomplete_all -> {
                            onEventListener.onCategoryCompleted(adapter.currentList)
                            remove(category)
                        }
                    }
                    true
                }
            }.show()
        }

        fun bind(category: Category, adapter: ItemsListAdapter) {
            this.adapter = adapter.apply {
                updateCategoryItemCount = { itemCount -> updateItemCount(binding.numItems, itemCount) }
            }
            this.category = category
            binding.categoryImage.setImageResource(category.drawable)
            binding.title.text = itemView.context.resources.getString(category.text)
            binding.itemsList.adapter = this.adapter

            if (category == Category.DONE) toggleCollapse()

            binding.options.setOnClickListener(showPopUpMenu)

            binding.collapse.setOnClickListener(toggleCollapse)

            updateItemCount(binding.numItems, adapter.itemCount)
        }

        private fun toggleCollapse() {
            binding.collapse.animate().apply {
                rotationXBy(180f)
            }.start()
            binding.apply {
                if (itemsList.isVisible) {
                    itemsList.visibility = View.GONE
                    divider.visibility = View.GONE
                } else {
                    itemsList.visibility = View.VISIBLE
                    divider.visibility = View.VISIBLE
                }
            }
        }
    }
}

interface CategoryAdapterEventListener {
    fun onItemClicked(item: ShoppingItem)
    fun onItemLongClicked(item: ShoppingItem)
    fun onRemoveButtonClicked(item: ShoppingItem)
    fun onCategoriesListCleared()
    fun onCategoriesListCompleted(list: List<ShoppingItem>)
    fun onCategoryAdded()
    fun onCategoryCleared(list: List<ShoppingItem>)
    fun onCategoryCompleted(list: List<ShoppingItem>)
}