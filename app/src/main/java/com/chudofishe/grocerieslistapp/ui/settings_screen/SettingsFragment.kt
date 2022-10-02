package com.chudofishe.grocerieslistapp.ui.settings_screen

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.chudofishe.grocerieslistapp.R

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings_screen)
    }
}