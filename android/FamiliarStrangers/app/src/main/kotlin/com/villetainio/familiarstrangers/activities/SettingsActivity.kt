package com.villetainio.familiarstrangers.activities

import android.os.Bundle
import android.preference.PreferenceActivity
import com.villetainio.familiarstrangers.R
import com.villetainio.familiarstrangers.fragments.SettingsFragment

class SettingsActivity : PreferenceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    /**
     * Populate the activity with the top-level headers.
     */
    override fun onBuildHeaders(target: List<Header>) {
        loadHeadersFromResource(R.xml.preference_headers, target)
    }

    /**
     * Check if the fragment is valid.
     */
    override fun isValidFragment(fragmentName: String) : Boolean {
        return SettingsFragment::class.java.name.equals(fragmentName)
    }
}
