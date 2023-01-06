package com.waffle22.wafflytime.ui.preferences

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.waffle22.wafflytime.BuildConfig
import com.waffle22.wafflytime.R

class SettingsFragment: PreferenceFragmentCompat() {

    lateinit var prefs: SharedPreferences

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.fragment_settings, rootKey)
        findPreference<Preference>("app_version")?.summary = BuildConfig.VERSION_NAME
    }
}