package com.chudofishe.grocerieslistapp.ui.settings_screen

import android.app.AlertDialog
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.chudofishe.grocerieslistapp.R
import com.chudofishe.grocerieslistapp.ui.MainActivity

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings_screen)
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        when (preference.key) {
            getString(R.string.settings_storage_delete_all_favorite_products) -> {
                AlertDialog.Builder(context)
                    .setMessage(R.string.settings_storage_delete_all_favorite_products_dialog)
                    .setPositiveButton(R.string.yes) { _, _ ->
                        (requireActivity() as MainActivity).clearFavoriteProducts()
                    }
                    .setNegativeButton(R.string.no, null)
                    .create().show()
            }
            getString(R.string.settings_storage_delete_all_lists) -> {
                AlertDialog.Builder(context)
                    .setMessage(R.string.settings_storage_delete_all_lists_dialog)
                    .setPositiveButton(R.string.yes) { _, _ ->
                        (requireActivity() as MainActivity).clearShoppingListHistory(true)
                    }
                    .setNegativeButton(R.string.no) {_, _ ->
                        (requireActivity() as MainActivity).clearShoppingListHistory(false)
                    }
                    .setNeutralButton(R.string.cancel, null)
                    .create().show()
            }
        }
        return super.onPreferenceTreeClick(preference)
    }
}