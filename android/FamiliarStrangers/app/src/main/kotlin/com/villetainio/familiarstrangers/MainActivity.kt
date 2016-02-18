package com.villetainio.familiarstrangers

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import com.firebase.client.Firebase
import com.villetainio.familiarstrangers.activities.LoginActivity
import com.villetainio.familiarstrangers.util.Constants
import com.villetainio.familiarstrangers.R
import com.villetainio.familiarstrangers.activities.OnBoardingActivity

import org.jetbrains.anko.*

class MainActivity : AppCompatActivity() {
    val firebase = Firebase(Constants.SERVER_URL)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        verticalLayout {
            val name = editText()
            button("Say Hello") {
                onClick { toast("Hello, ${name.text}!") }
            }

            val signOut = textView("Sign out") {
                isClickable
                onClick {
                    firebase.unauth()
                    checkAuthenticationStatus()
                }
            }
        }

        checkAuthenticationStatus()
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
