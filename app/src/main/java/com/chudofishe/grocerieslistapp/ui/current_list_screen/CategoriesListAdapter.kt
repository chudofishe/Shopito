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
    : RecyclerView.Adapter<CategoriesListAdapter.ViewHolder>(), ItemsListAdapterActionsListener {

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

    override fun onItemClicked(item: ShoppingItem) {
//        if (categoryAdapters[item.currentCategory]?.items?.size == 1) {
//            remove(item.currentCategory)
//        }
        onEventListener.onItemClicked(item)
    }

    override fun onItemLongClicked(item: ShoppingItem) {
        onEventListener.onItemLongClicked(item)
    }

    override fun onRemoveButtonClicked(item: ShoppingItem) {
//        if (categoryAdapters[item.currentCategory]?.items?.size == 1) {
//            remove(item.currentCategory)
//        }
        onEventListener.onRemoveButtonClicked(item)
    }

//    fun addItem(item: ShoppingItem) {
//        if (!categories.contains(item.currentCategory)) {
//            add(item.currentCategory)
//        }
//        categoryAdapters[item.currentCategory]!!.add(item)
//    }

    fun getItems(): List<ShoppingItem> {
        val items = mutableListOf<ShoppingItem>()
        categoryAdapters.forEach {
            items.addAll(it.value.items)
        }
        return items
    }

    fun setList(list: List<ShoppingItem>) {
        if (list.isCompleted()) {
            onEventListener.onCompleted(list)
        } else {
            Category.values().forEach { category ->
                val filteredList = list.filter { x -> x.currentCategory ==  category }
                if (filteredList.isEmpty()) {
                    if (categories.contains(category)) {
                        val index = categories.indexOf(category)
                        categories.remove(category)
                        notifyItemRemoved(index)
                        categoryAdapters.remove(category)
                    }
                } else {
                    addListToCategory(filteredList, category)
                }
            }
        }
    }

    private fun List<ShoppingItem>.isCompleted(): Boolean {
        this.forEach { if (it.currentCategory != Category.DONE) return false }
        return true
    }
    private fun addListToCategory(list: List<ShoppingItem>, category: Category) {
        if (!categories.contains(category)) {
            addCategory(category)
        }
        categoryAdapters[category]!!.setItemsList(list)
    }

    private fun addCategory(newItem: Category) {
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
            ItemsListAdapter(ItemsListAdapterItemType.ACTIVE, this)
        onEventListener.onCategoryAdded()
    }

//    @SuppressLint("NotifyDataSetChanged")
//    private fun remove(category: Category) {
//        val index = categories.indexOf(category)
//        categories.remove(category)
//        notifyItemRemoved(index)
//        categoryAdapters.remove(category)
//        if (categories.size == 0) {
//            onEventListener.onCleared()
//        }
//        if (categories.size == 1 && categories[0] == Category.DONE) {
//            onEventListener.onCompleted(getItems())
//        }
//    }

    @SuppressLint("NotifyDataSetChanged")
    fun clear() {
        categories.clear()
        categoryAdapters.clear()
        notifyDataSetChanged()
    }

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
                            onEventListener.onCategoryCleared(adapter.items)
                        }
                        R.id.menu_complete_all, R.id.menu_uncomplete_all -> {
                            onEventListener.onCategoryCompleted(adapter.items)
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

