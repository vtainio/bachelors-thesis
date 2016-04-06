package com.villetainio.familiarstrangers.activities

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
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
import com.villetainio.familiarstrangers.R
import com.villetainio.familiarstrangers.fragments.ProfileFragment
import com.villetainio.familiarstrangers.util.Constants

class EncounterActivity : AppCompatActivity() {
    val firebase = Firebase(Constants.SERVER_URL)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_encounter)

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
                                .withName(getString(R.string.activity_label_settings))
                                .withIdentifier(Constants.MENU_SETTINGS),
                        PrimaryDrawerItem()
                                .withName(getString(R.string.menu_logout))
                                .withIdentifier(Constants.MENU_LOGOUT)
                )
                .withOnDrawerItemClickListener(object: Drawer.OnDrawerItemClickListener {
                    override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*, *>?): Boolean {
                        when (drawerItem?.identifier) {
                            Constants.MENU_LOGOUT -> logout()
                            Constants.MENU_SETTINGS -> startActivity(Intent(this@EncounterActivity, SettingsActivity::class.java))
                        }

                        return false
                    }
                })
                .build();

        val mAdapter = SliderAdapter(supportFragmentManager, intent.extras.getString("userId"))
        val mPager = findViewById(R.id.encounterPager) as ViewPager
        mPager.adapter = mAdapter
    }

    fun logout() {
        firebase.unauth()
        finish()
    }

    class SliderAdapter : FragmentStatePagerAdapter {
        var userId: String? = null
        constructor(fm: FragmentManager, id: String?) : super(fm) {
            userId = id
        }

        override fun getItem(position: Int): Fragment? {
            if (userId != null) {
                return ProfileFragment.newInstance(userId!!)
            } else {
                return ProfileFragment()
            }
        }

        override fun getCount(): Int {
            return 1
        }
    }
}
