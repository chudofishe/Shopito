package com.chudofishe.grocerieslistapp.ui.active_list_screen

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.addCallback
import androidx.core.view.MenuProvider
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
import com.chudofishe.grocerieslistapp.ui.common.util.*
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
    private lateinit var toolTipAnim: LottieAnimationView
    private lateinit var toolTipText: TextView
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
            toolTipAnim = anim
            toolTipText = tooltipText
            addFavoriteButton = binding.bottomSheet.addFavoriteButton
            this@ActiveListFragment.titleInput = titleInput
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
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.activeListState.collect { wrapper ->
                    wrapper.state.title?.let {
                        if (titleInput.editText?.text.toString() != it) {
                            titleInput.editText?.setText(it)
                        }
                    }
                    categoriesAdapter.setList(wrapper.state.items)
                    setToolTip(wrapper.listStatus)
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.showOnCompletedDialog.collect {
                    if (it) {
                        setToolTip()
                    }
                }
            }
        }

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.active_list_toolbar_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.action_clear_list -> viewModel.clearItemsList()
                    R.id.action_mark_list_as_done -> viewModel.completeItemsList()
                    else -> viewModel.navigateBack()
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        categoriesAdapter = CategoriesListAdapter(this, viewModel.collapseDoneCategory)
        categoriesList.adapter = categoriesAdapter
        categoriesList.addItemDecoration(
            MarginItemDecoration(resources.getDimension(R.dimen.card_spacing).toInt())
        )

        toolTipAnim.addAnimatorListener(object : AnimatorListener {
            override fun onAnimationEnd(animation: Animator?) {
                binding.tooltipText.visibility = View.VISIBLE
                binding.tooltipText.text = getString(R.string.tooltip_active_list_done)
            }
        })

        submitButton.setOnClickListener {
            if (!itemName.text.isNullOrEmpty()) {
                val category = if (categoriesGroup.checkedChipId == View.NO_ID) {
                    Category.UNCATEGORIZED
                } else {
                    Category.values()[categoriesGroup.checkedChipId]
                }
                viewModel.addItem(
                    ShoppingItem(
                        text = itemName.text.toString(),
                        description = itemDescription.text?.toStringOrNull(),
                        currentCategory = category,
                        originalCategory = category
                    )
                )
                itemName.text?.clear()
                itemDescription.text?.clear()
            }
        }

        titleInput.editText?.addTextChangedListener(object : TextWatcher {
            private val handler = Handler(Looper.getMainLooper())
            private val runnable = Runnable {
                viewModel.updateTitle(titleInput.editText?.text.toStringOrNull())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handler.removeCallbacks(runnable)
            }

            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrEmpty()) {
                    handler.postDelayed(runnable, 1000)
                }
            }
        })

        addFavoriteButton.setOnClickListener {
            val direction =
                ActiveListFragmentDirections.actionActiveListDestinationToFavoriteProductsFragment(
                    true
                )
            viewModel.navigate(direction)
        }

        //Add category chips to bottom sheet
        categoriesGroup.init()
    }

    override fun onStart() {
        super.onStart()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            } else {
                this.remove()
                requireActivity().onBackPressed()
            }
        }
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

    override fun onCategoryCleared(list: List<ShoppingItem>) {
        viewModel.removeItemsList(list)
    }

    override fun onCategoryCompleted(list: List<ShoppingItem>) {
        viewModel.updateItemsList(list)
    }

    fun saveCurrentState() {
        viewModel.saveCurrentStateToPrefs()
    }

    private fun setToolTip(status: ActiveListViewModel.ListStatus? = null) {
        when (status) {
            ActiveListViewModel.ListStatus.ACTIVE -> {
                toolTipText.visibility = View.GONE
                toolTipAnim.visibility = View.GONE
                titleInput.visibility = View.VISIBLE
            }
            ActiveListViewModel.ListStatus.EMPTY -> {
                toolTipAnim.apply {
                    visibility = View.VISIBLE
                    setAnimation(R.raw.empty)
                    repeatCount = ValueAnimator.INFINITE
                    playAnimation()
                }
                toolTipText.apply {
                    fadeIn(500)
                    setText(R.string.tooltip_active_list_empty)
                }
                titleInput.visibility = View.INVISIBLE
                titleInput.editText?.text?.clear()
            }
            else -> {
                toolTipAnim.apply {
                    visibility = View.VISIBLE
                    setAnimation(R.raw.done)
                    repeatCount = 0
                    playAnimation()
                    addAnimatorListener(object : Animator.AnimatorListener {
                        override fun onAnimationEnd(animation: Animator?) {
                            toolTipText.apply {
                                setText(R.string.tooltip_active_list_done)
                            }
                        }

                        override fun onAnimationCancel(animation: Animator?) {}
                        override fun onAnimationRepeat(animation: Animator?) {}
                        override fun onAnimationStart(animation: Animator?) {}
                    })
                }
                titleInput.visibility = View.INVISIBLE
                titleInput.editText?.text?.clear()
            }
        }
    }

    private fun hideKeyboard(view: View) {
        view.clearFocus()
        val imm = view.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}