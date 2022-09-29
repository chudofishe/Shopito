package com.chudofishe.grocerieslistapp.ui.onboarding_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.chudofishe.grocerieslistapp.R
import com.chudofishe.grocerieslistapp.databinding.FragmentOnboardingBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textview.MaterialTextView

class OnBoardingFragment : Fragment() {

    private var _binding: FragmentOnboardingBinding? = null
    private val binding: FragmentOnboardingBinding
        get() = _binding!!

    private lateinit var onBoardingTitle: MaterialTextView
    private lateinit var onBoardingSubTitle: MaterialTextView
    private lateinit var lottieAnimation: LottieAnimationView
    private lateinit var navButton: MaterialButton


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)

        binding.apply {
            this@OnBoardingFragment.onBoardingTitle = onBoardingTitle
            this@OnBoardingFragment.onBoardingSubTitle = onBoardingSubtitle
            this@OnBoardingFragment.lottieAnimation = lottieAnimation
            this@OnBoardingFragment.navButton = navigateButton
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.takeIf { it.containsKey(OnBoardingConstants.ARG_KEY_PAGE_NUM) }?.let {
            when (it.getInt(OnBoardingConstants.ARG_KEY_PAGE_NUM)) {
                0 -> {
                    lottieAnimation.apply {
                        setAnimation(R.raw.onboarding1)
                        repeatMode = LottieDrawable.REVERSE
                    }
                    onBoardingTitle.setText(R.string.onboarding_title_1)
                    onBoardingSubTitle.setText(R.string.onboarding_subtitle_1)
                }
                1 -> {
                    lottieAnimation.apply {
                        setAnimation(R.raw.onboarding2)
                        repeatCount = 1
                    }
                    onBoardingTitle.setText(R.string.onboarding_title_2)
                    onBoardingSubTitle.setText(R.string.onboarding_subtitle_2)
                }
                else -> {
                    lottieAnimation.setAnimation(R.raw.onboarding3)
                    onBoardingTitle.setText(R.string.onboarding_title_3)
                    onBoardingSubTitle.setText(R.string.onboarding_subtitle_3)
                    navButton.visibility = View.VISIBLE
                }
            }
        }

        parentFragment?.let {
            if (parentFragment is OnBoardingHostFragment) {
                val parent = parentFragment as OnBoardingHostFragment
                navButton.setOnClickListener {
                    parent.finishUnBoarding()
                }
            }
        }
    }

}