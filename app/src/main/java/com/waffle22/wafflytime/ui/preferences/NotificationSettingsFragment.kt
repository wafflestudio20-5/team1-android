package com.waffle22.wafflytime.ui.preferences

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.waffle22.wafflytime.R

class NotificationSettingsFragment: PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.fragment_notification_settings, rootKey)
    }

}