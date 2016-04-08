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
import com.firebase.client.Firebase
import com.firebase.client.FirebaseError
import org.jetbrains.anko.*

import com.villetainio.familiarstrangers.R
import com.villetainio.familiarstrangers.util.Constants

import android.text.InputType.TYPE_CLASS_TEXT
import android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
import android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
import com.firebase.client.AuthData

class RegisterActivity : AppCompatActivity() {
    val firebase = Firebase(Constants.SERVER_URL)
    val onBoardingRequestCode = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /**
         * Define layout for the activity.
         */
        verticalLayout {
            val emailLabel = textView(getString(R.string.label_email))
            val email = editText {
                inputType = TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            }
            val passwordLabel = textView(getString(R.string.label_password))
            val password = editText {
                inputType = TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_PASSWORD
            }
            val validationLabel = textView(getString(R.string.label_password_validation))
            val passwordValidate = editText {
                inputType = TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_PASSWORD
            }
            button(getString(R.string.label_register)) {
                onClick {
                    register(email.text.toString(), password.text.toString(), passwordValidate.text.toString())
                }

            }
        }
    }

    /**
     * Check if onboarding has ended successfully.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == onBoardingRequestCode && resultCode == RESULT_OK) {
            setResult(RESULT_OK)
            finish()
        }
    }

    /**
     * If passwords match, continue to registering to Firebase,
     * otherwise display an error on the screen.
     */
    fun register(email: String, password: String, validation: String) {
        if (passwordsMatch(password, validation)) {
            registerToFirebase(email, password)
        } else {
            Toast.makeText(this, getString(R.string.error_passwords_do_not_match), Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Returns true if the two string match, false otherwise.
     */
    fun passwordsMatch(first: String, second: String) : Boolean {
        return first.equals(second)
    }

    /**
     * Register to firebase with the given email and password.
     */
    fun registerToFirebase(email: String, password: String) {
        firebase.createUser(email, password, object: Firebase.ValueResultHandler<Map<String, Any>> {

            override fun onSuccess(result: Map<String, Any>) {
                loginAutomatically(email, password)
            }

            override fun onError(firebaseError: FirebaseError) {
                // Display error.
                Toast.makeText(applicationContext, firebaseError.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    /**
     * Firebase doesn't log in automatically after register,
     * so this does it by hand.
     */
    fun loginAutomatically(email: String, password: String) {
        firebase.authWithPassword(email, password, object: Firebase.AuthResultHandler {

            override fun onAuthenticated(authData: AuthData) {
                storeUserIdAndEmail(authData.uid, email)
                startOnBoarding()
            }

            override fun onAuthenticationError(firebaseError: FirebaseError) {
                Toast.makeText(applicationContext, getString(R.string.error_default), Toast.LENGTH_LONG).show()
                finish()
            }
        })
    }

    fun storeUserIdAndEmail(userId: String, email: String) {
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putString(getString(R.string.settings_uid), userId)
                .putString(getString(R.string.settings_email), email)
                .apply()
    }

    /**
     * Start onboarding activity.
     */
    fun startOnBoarding() {
        val onBoardingIntent = Intent(this@RegisterActivity, OnBoardingActivity::class.java)
        startActivityForResult(onBoardingIntent,onBoardingRequestCode)
    }
}
