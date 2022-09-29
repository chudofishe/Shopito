package com.chudofishe.grocerieslistapp.ui.favorites_screen

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chudofishe.grocerieslistapp.R
import com.chudofishe.grocerieslistapp.data.model.Category
import com.chudofishe.grocerieslistapp.data.model.ShoppingItem
import com.chudofishe.grocerieslistapp.databinding.FragmentFavoriteProductsBinding
import com.chudofishe.grocerieslistapp.databinding.FragmentHistoryListBinding
import com.chudofishe.grocerieslistapp.ui.common.BaseFragment
import com.chudofishe.grocerieslistapp.ui.common.ItemsListAdapter
import com.chudofishe.grocerieslistapp.ui.common.ItemsListAdapterActionsListener
import com.chudofishe.grocerieslistapp.ui.common.ItemsListAdapterItemType
import com.chudofishe.grocerieslistapp.ui.common.util.MarginItemDecoration
import com.chudofishe.grocerieslistapp.ui.common.util.init
import com.chudofishe.grocerieslistapp.ui.common.util.toStringOrNull
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoriteProductsFragment : BaseFragment<FavoriteProductsViewModel>() {
    private var _binding: FragmentFavoriteProductsBinding? = null
    private val binding: FragmentFavoriteProductsBinding
        get() = _binding!!

    override val viewModel: FavoriteProductsViewModel by viewModels()

    private lateinit var productsList: RecyclerView
    private lateinit var submitButton: ImageButton
    private lateinit var itemName: TextInputEditText
    private lateinit var itemDescription: TextInputEditText
    private lateinit var categoriesGroup: ChipGroup
    private lateinit var adapter: ItemsListAdapter
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    private var editedItemId: Int? = null

    private val itemsListAdapterActionsListener = object : ItemsListAdapterActionsListener {
        override fun onUpdateButtonClicked(item: ShoppingItem) {
            editedItemId = item.id
            itemName.setText(item.text)
            itemDescription.setText(item.description)
            if (item.originalCategory != Category.UNCATEGORIZED) {
                categoriesGroup.check(item.originalCategory.ordinal)
            }
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        override fun onRemoveButtonClicked(item: ShoppingItem) {
            viewModel.delete(item)
        }

//        override fun saveItem(item: ShoppingItem) {
//            viewModel.add(item)
//        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteProductsBinding.inflate(inflater, container, false)

        binding.apply {
            this@FavoriteProductsFragment.productsList = productsList
            submitButton = bottomSheet.submit
            itemName = bottomSheet.itemName
            itemDescription = bottomSheet.itemDescription
            categoriesGroup = bottomSheet.categoriesChipGroup

            bottomSheet.addFavoriteButton.visibility = View.GONE
        }

        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet.root).apply {
            peekHeight = resources.getDimension(R.dimen.input_group_peek_height).toInt()
            state = BottomSheetBehavior.STATE_COLLAPSED
            isHideable = false
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.productsList.collect {
                    adapter.submitList(it)
                }
            }
        }

        if (viewModel.isSelectionMode) {
            val menuHost: MenuHost = requireActivity()
            menuHost.addMenuProvider(object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.favorite_products_toolbar_menu, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    when(menuItem.itemId) {
                        R.id.action_confirm -> {
                            val direction =
                                FavoriteProductsFragmentDirections.actionFavoriteProductsFragmentToCurrentListDestination(selectedFavItems = adapter.checkedItems.toTypedArray())
                            viewModel.navigate(direction)
                        }
                        else -> viewModel.navigateBack()
                    }
                    return true
                }
            }, viewLifecycleOwner, Lifecycle.State.RESUMED)
        }

        adapter = if (viewModel.isSelectionMode) {
            ItemsListAdapter(ItemsListAdapterItemType.FAVORITE_SELECTION, itemsListAdapterActionsListener)
        } else {
            ItemsListAdapter(ItemsListAdapterItemType.FAVORITE_EDIT, itemsListAdapterActionsListener)
        }

        productsList.adapter = adapter

        submitButton.setOnClickListener {
            if (!itemName.text.isNullOrEmpty()) {
                val category = if (categoriesGroup.checkedChipId == View.NO_ID) {
                    Category.UNCATEGORIZED
                } else {
                    Category.values()[categoriesGroup.checkedChipId]
                }
                if (editedItemId != null) {
                    viewModel.update(ShoppingItem(
                        id = editedItemId!!,
                        text = itemName.text.toString(),
                        description = itemDescription.text?.toStringOrNull(),
                        currentCategory = category,
                        originalCategory = category
                    ))
                    editedItemId = null
                } else {
                    viewModel.add(ShoppingItem(
                        text = itemName.text.toString(),
                        description = itemDescription.text?.toStringOrNull(),
                        currentCategory = category,
                        originalCategory = category
                    ))
                }
                itemName.text?.clear()
                itemDescription.text?.clear()
            }
        }

        //Add category chips to bottom sheet
        categoriesGroup.init()
        productsList.addItemDecoration(
            MarginItemDecoration(resources.getDimension(R.dimen.shopping_item_spacing).toInt())
//            DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}