/*
* Copyright 2016 Ville Tainio
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

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
