package com.chudofishe.grocerieslistapp.ui.favorites_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.chudofishe.grocerieslistapp.R
import com.chudofishe.grocerieslistapp.databinding.FragmentFavoritesBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class FavoritesFragment : Fragment() {
    private var _binding: FragmentFavoritesBinding? = null
    private val binding: FragmentFavoritesBinding
        get() = _binding!!

    private lateinit var pager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)

        binding.apply {
            this@FavoritesFragment.pager = pager
            this@FavoritesFragment.tabLayout = tabLayout
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pager.adapter = TabLayoutAdapter(this)
        TabLayoutMediator(tabLayout, pager) { tab, pos ->
            tab.text = when(pos) {
                0 -> getString(R.string.tab_lists)
                else -> getString(R.string.tab_products)
            }
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

private class TabLayoutAdapter(parent: Fragment) : FragmentStateAdapter(parent) {
    override fun getItemCount(): Int  = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FavoriteListsFragment()
            else -> FavoriteProductsFragment()
        }
    }
}