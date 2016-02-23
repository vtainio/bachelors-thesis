package com.villetainio.familiarstrangers.activities

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.firebase.client.Firebase
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.villetainio.familiarstrangers.util.Constants
import com.villetainio.familiarstrangers.R

class MainActivity : AppCompatActivity() {
    val firebase = Firebase(Constants.SERVER_URL)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val name =  PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getString(R.string.settings_full_name), "")

        val toolbar = findViewById(R.id.fs_toolbar) as Toolbar
        setSupportActionBar(toolbar)


        val headerResult = AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.color.material_drawer_background)
                .withSelectionListEnabledForSingleProfile(false)
                .addProfiles(
                        ProfileDrawerItem().withName(name)
                )
                .withOnAccountHeaderListener { view, iProfile, b -> false }
                .build()

        val result = DrawerBuilder().withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(false)
                .withAccountHeader(headerResult)
                .withActionBarDrawerToggle(true)
                .addDrawerItems(
                        // Pass items here.
                )
                .build();
    }

    override fun onResume() {
        super.onResume()
        checkAuthenticationStatus()
        checkOnBoardingStatus()
    }

    /**
     * Start Login activity if the user isn't authenticated.
     */
    fun checkAuthenticationStatus() {
        if (firebase.auth == null) {
            val loginIntent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(loginIntent)
        }
    }

    /**
     * Check that the user has completed their onboarding.
     */
    fun checkOnBoardingStatus() {
       val fullName = PreferenceManager.getDefaultSharedPreferences(this)
            .getString(getString(R.string.settings_full_name), "")

        if (fullName.length == 0) {
            val onBoardingIntent = Intent(this@MainActivity, OnBoardingActivity::class.java)
            startActivity(onBoardingIntent)
        }
    }
}
