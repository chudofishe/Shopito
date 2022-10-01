package com.chudofishe.grocerieslistapp.ui.onboarding_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.chudofishe.grocerieslistapp.databinding.FragmentOnboardingHostBinding
import com.chudofishe.grocerieslistapp.ui.common.BaseFragment
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnBoardingHostFragment : BaseFragment<OnBoardingViewModel>() {

    private var _binding: FragmentOnboardingHostBinding? = null
    private val binding: FragmentOnboardingHostBinding
        get() = _binding!!

    override val viewModel: OnBoardingViewModel by viewModels()

    private lateinit var pager: ViewPager2
    private lateinit var dotsIndicator: DotsIndicator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingHostBinding.inflate(inflater, container, false)

        binding.apply {
            this@OnBoardingHostFragment.pager = pager
            this@OnBoardingHostFragment.dotsIndicator = dotsIndicator
        }

        (activity as AppCompatActivity).supportActionBar?.hide()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pager.adapter = TabLayoutAdapter(this)
        dotsIndicator.attachTo(pager)
    }

    fun finishUnBoarding() {
        (activity as AppCompatActivity).supportActionBar?.show()
        viewModel.saveWatchedOnBoarding()
        val action = OnBoardingHostFragmentDirections.actionOnBoardingHostFragmentToActiveListDestination()
        viewModel.navigate(action)
    }

}

private class TabLayoutAdapter(parent: Fragment) : FragmentStateAdapter(parent) {
    override fun getItemCount(): Int  = 3

    override fun createFragment(position: Int): Fragment {
        val fragment = OnBoardingFragment()
        fragment.arguments = Bundle().apply {
            putInt(OnBoardingConstants.ARG_KEY_PAGE_NUM, position)
        }
        return fragment
    }
}