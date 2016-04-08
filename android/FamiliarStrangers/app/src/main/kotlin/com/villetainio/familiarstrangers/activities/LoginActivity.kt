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

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.firebase.client.AuthData
import com.firebase.client.Firebase
import com.firebase.client.FirebaseError

import org.jetbrains.anko.*

import com.villetainio.familiarstrangers.R
import com.villetainio.familiarstrangers.util.Constants

import android.text.InputType.TYPE_CLASS_TEXT
import android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
import android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

class LoginActivity : AppCompatActivity() {
    val firebase = Firebase(Constants.SERVER_URL)
    val registerRequestCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /**
         * Define layout for the activity.
         */
        verticalLayout {
            val emailLabel = textView(getString(R.string.label_email))
            val email = editText {
                inputType = TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            }
            val passwordLabel = textView(getString(R.string.label_password))
            val password = editText {
                inputType = TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_PASSWORD
            }
            button(getString(R.string.label_login)) {
                onClick {
                    login(email.text.toString(), password.text.toString())
                }
            }
            val registerLink = textView(getString(R.string.label_register)) {
                isClickable
                onClick {
                    startRegisterActivity()
                }
            }
        }
    }

    /**
     * Receive code whether registering was successful or not.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == registerRequestCode && resultCode == RESULT_OK) {
            // The user has authenticated in the register view -> Leave login
            finish()
        }
    }

    /**
     * Login the user.
     * If the login is successful, store the information in preferences.
     * Otherwise display an error message on the screen.
     */
    fun login(email: String, password: String) {
        firebase.authWithPassword(email, password, object: Firebase.AuthResultHandler {

            override fun onAuthenticated(authData : AuthData) {
                // Save login data to shared preferences
                PreferenceManager.getDefaultSharedPreferences(applicationContext)
                        .edit()
                        .putString(getString(R.string.settings_uid), authData.uid)
                        .putString(getString(R.string.settings_email), email)
                        .apply()
                finish() // Return to the parent activity.
            }

            override fun onAuthenticationError(firebaseError : FirebaseError) {
                // Display error.
                when (firebaseError.code) {
                    FirebaseError.USER_DOES_NOT_EXIST ->
                            loginError(getString(R.string.error_user_does_not_exits))
                    FirebaseError.INVALID_PASSWORD ->
                            loginError(getString(R.string.error_invalid_password))
                    else ->
                            loginError(getString(R.string.error_default))
                }
            }
        })
    }

    /**
     * Helper function to show the error message.
     */
    fun loginError(message: String, length: Int = Toast.LENGTH_LONG) {
        Toast.makeText(this, message, length).show()
    }

    /**
     * Start the register activity with the request code defined in registerRequestCode.
     */
    fun startRegisterActivity() {
        val registerIntent = Intent(this@LoginActivity, RegisterActivity::class.java)
        startActivityForResult(registerIntent, registerRequestCode)
    }
}
