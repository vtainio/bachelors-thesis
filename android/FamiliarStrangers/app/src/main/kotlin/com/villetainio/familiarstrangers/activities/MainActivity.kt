package com.villetainio.familiarstrangers.activities

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import com.firebase.client.Firebase
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import com.villetainio.familiarstrangers.util.Constants
import com.villetainio.familiarstrangers.R

class MainActivity : AppCompatActivity() {
    val firebase = Firebase(Constants.SERVER_URL)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val name =  preferences.getString(getString(R.string.settings_full_name), "")
        val email = preferences.getString(getString(R.string.settings_email), "")

        val toolbar = findViewById(R.id.fs_toolbar) as Toolbar
        setSupportActionBar(toolbar)


        val headerResult = AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.color.material_drawer_background)
                .withSelectionListEnabledForSingleProfile(false)
                .addProfiles(
                        ProfileDrawerItem()
                                .withEmail(email)
                                .withName(name)
                )
                .withOnAccountHeaderListener { view, iProfile, b -> false }
                .build()

        DrawerBuilder().withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(false)
                .withAccountHeader(headerResult)
                .withActionBarDrawerToggle(true)
                .addDrawerItems(
                        PrimaryDrawerItem()
                            .withName(getString(R.string.menu_logout))
                            .withIdentifier(Constants.MENU_LOGOUT)
                )
                .withOnDrawerItemClickListener(object: Drawer.OnDrawerItemClickListener {
                    override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*,*>?): Boolean {
                        when (drawerItem?.identifier) {
                            Constants.MENU_LOGOUT -> logout()
                        }

                        return false
                    }
                })
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

    /**
     * Logout the user.
     */
    fun logout() {
        firebase.unauth()
        checkAuthenticationStatus()
    }
}