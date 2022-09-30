package com.chudofishe.grocerieslistapp.ui.current_list_screen

import android.animation.Animator
import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.constraintlayout.widget.Group
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.chudofishe.grocerieslistapp.R
import com.chudofishe.grocerieslistapp.data.model.Category
import com.chudofishe.grocerieslistapp.data.model.ShoppingItem
import com.chudofishe.grocerieslistapp.databinding.FragmentActiveListBinding
import com.chudofishe.grocerieslistapp.ui.common.BaseFragment
import com.chudofishe.grocerieslistapp.ui.common.util.AnimatorListener
import com.chudofishe.grocerieslistapp.ui.common.util.MarginItemDecoration
import com.chudofishe.grocerieslistapp.ui.common.util.init
import com.chudofishe.grocerieslistapp.ui.common.util.toStringOrNull
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ActiveListFragment : BaseFragment<ActiveListViewModel>(), CategoryAdapterEventListener {
    private var _binding: FragmentActiveListBinding? = null
    private val binding: FragmentActiveListBinding
        get() = _binding!!

    override val viewModel: ActiveListViewModel by viewModels()

    private lateinit var submitButton: ImageButton
    private lateinit var addFavoriteButton: MaterialButton
    private lateinit var itemName: TextInputEditText
    private lateinit var itemDescription: TextInputEditText
    private lateinit var titleInput: TextInputLayout
    private lateinit var categoriesGroup: ChipGroup
    private lateinit var categoriesAdapter: CategoriesListAdapter
    private lateinit var categoriesList: RecyclerView
    private lateinit var doneAnimation: LottieAnimationView
    private lateinit var emptyListTooltip: Group
    private lateinit var scrollView: NestedScrollView
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentActiveListBinding.inflate(inflater, container, false)

        binding.apply {
            categoriesGroup = bottomSheet.categoriesChipGroup
            categoriesList = categories
            submitButton = bottomSheet.submit
            itemName = bottomSheet.itemName
            itemDescription = bottomSheet.itemDescription
            doneAnimation = doneAnim
            emptyListTooltip = tooltipGroup
            addFavoriteButton = binding.bottomSheet.addFavoriteButton
            this@ActiveListFragment.titleInput = titleInput
            this@ActiveListFragment.scrollView = scrollView
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
                viewModel.activeListState.collect { wrapper ->
                    wrapper.state.title?.let { titleInput.editText?.setText(it) }
                    if (wrapper.state.items.isEmpty()) {
                        showEmptyListToolTip()
                    } else {
                        categoriesAdapter.setList(wrapper.state.items)
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectedItems.collect {
                    categoriesAdapter.setList(it.toList())
                }
            }
        }

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.active_list_toolbar_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when(menuItem.itemId) {
                    R.id.action_clear_list -> clearCategoriesList()
                    R.id.action_mark_list_as_done -> {
                        completeCategoriesList(categoriesAdapter.getItems())
                    }
                    else -> viewModel.navigateBack()
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        categoriesAdapter = CategoriesListAdapter(onEventListener = this)
        categoriesList.adapter = categoriesAdapter
        categoriesList.addItemDecoration(
            MarginItemDecoration(resources.getDimension(R.dimen.card_spacing).toInt())
        )

        doneAnimation.addAnimatorListener(object : AnimatorListener {
            override fun onAnimationEnd(animation: Animator?) {
                binding.tooltipText.visibility = View.VISIBLE
                binding.tooltipText.text = getString(R.string.tooltip_done_text)
            }
        })

        submitButton.setOnClickListener {
            if (!itemName.text.isNullOrEmpty()) {
                val category = if (categoriesGroup.checkedChipId == View.NO_ID) {
                    Category.UNCATEGORIZED
                } else {
                    Category.values()[categoriesGroup.checkedChipId]
                }
                viewModel.addItem(ShoppingItem(
                    text = itemName.text.toString(),
                    description = itemDescription.text?.toStringOrNull(),
                    currentCategory = category,
                    originalCategory = category
                ))
                itemName.text?.clear()
                itemDescription.text?.clear()
//                    categoriesGroup.clearCheck()
//                    hideKeyboard(itemInput)
            }
        }

        addFavoriteButton.setOnClickListener {
            val direction = ActiveListFragmentDirections.actionCurrentListDestinationToFavoriteProductsFragment(true)
            viewModel.navigate(direction)
        }

        //Add category chips to bottom sheet
        categoriesGroup.init()
    }

    override fun onItemClicked(item: ShoppingItem) {
        viewModel.updateItem(item)
    }

    override fun onItemLongClicked(item: ShoppingItem) {
        viewModel.addShoppingItemToFavorites(item)
    }

    override fun onRemoveButtonClicked(item: ShoppingItem) {
        viewModel.removeItem(item)
    }

    override fun onCleared() {
        clearCategoriesList()
    }

    override fun onCompleted(list: List<ShoppingItem>) {
        completeCategoriesList(list)
    }

    override fun onCategoryAdded() {
        if (emptyListTooltip.isVisible || doneAnimation.isVisible) {
            emptyListTooltip.visibility = View.GONE
            doneAnimation.visibility = View.INVISIBLE
            titleInput.visibility = View.VISIBLE
        }
    }

    override fun onCategoryCleared(list: List<ShoppingItem>) {
        viewModel.removeItemsList(list)
    }

    override fun onCategoryCompleted(list: List<ShoppingItem>) {
        viewModel.updateItemsList(list)
    }

    private fun completeCategoriesList(list: List<ShoppingItem>) {
        if (emptyListTooltip.isVisible) emptyListTooltip.visibility = View.GONE
        doneAnimation.visibility = View.VISIBLE
        doneAnimation.playAnimation()
        titleInput.visibility = View.INVISIBLE
        titleInput.editText?.text?.clear()

        val title = titleInput.editText?.text.toStringOrNull()
        categoriesAdapter.clear()

        viewModel.saveList(title, list)
    }

    private fun clearCategoriesList() {
        showEmptyListToolTip()

        viewModel.clearState()
    }

    override fun onPause() {
        super.onPause()
        _binding = null

        val title = titleInput.editText?.text.toStringOrNull()
        viewModel.saveTempState(title, categoriesAdapter.getItems())
    }

    private fun showEmptyListToolTip() {
        titleInput.visibility = View.INVISIBLE
        titleInput.editText?.text?.clear()
        doneAnimation.visibility = View.GONE
        emptyListTooltip.visibility = View.VISIBLE
        binding.tooltipText.text = getString(R.string.tooltip_start_text)
    }

    private fun hideKeyboard(view: View) {
        view.clearFocus()
        val imm = view.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}