package com.villetainio.familiarstrangers.fragments

import android.os.Bundle
import android.preference.PreferenceFragment
import com.villetainio.familiarstrangers.R

class SettingsFragment : PreferenceFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.fragment_preference)
    }
}
