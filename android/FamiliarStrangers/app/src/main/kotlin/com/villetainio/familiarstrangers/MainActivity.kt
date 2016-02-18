package com.villetainio.familiarstrangers

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.firebase.client.Firebase
import com.villetainio.familiarstrangers.activities.LoginActivity
import com.villetainio.familiarstrangers.util.Constants

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
}
