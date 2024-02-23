package com.azamovhudstc.soplay.ui.screens.settings

import android.content.Intent
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.azamovhudstc.soplay.R
import com.azamovhudstc.soplay.ui.screens.bottomsheet.DevelopersDialogFragment
import com.azamovhudstc.soplay.utils.saveData
import com.google.android.material.snackbar.Snackbar
import kotlin.system.exitProcess


class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)


        val developerPref = findPreference<Preference>("developers")
        developerPref?.setOnPreferenceClickListener {
            DevelopersDialogFragment().show(parentFragmentManager, "dialog")
            true
        }


    }
}