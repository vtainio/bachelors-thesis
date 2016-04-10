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
import android.text.InputType
import android.widget.Toast
import com.firebase.client.Firebase
import org.jetbrains.anko.*
import com.villetainio.familiarstrangers.R
import com.villetainio.familiarstrangers.util.Constants

class OnBoardingActivity : AppCompatActivity() {
    val firebase = Firebase(Constants.SERVER_URL)
    val beaconRegisterRequestCode = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        verticalLayout {
            padding = dip(20)
            textView(getString(R.string.label_full_name))
            val fullName = editText()
            textView(getString(R.string.label_sex)) {
                topPadding = dip(10)
            }
            val sex = radioGroup({
                radioButton({
                    text = getString(R.string.label_male)
                })
                radioButton({
                    text = getString(R.string.label_female)
                })
            })
            textView(getString(R.string.label_age)) {
                topPadding = dip(10)
            }
            val age = editText {
                inputType = InputType.TYPE_CLASS_NUMBER
            }
            textView(getString(R.string.label_interests)) {
                topPadding = dip(10)
            }
            val interests = editText()
            button(getString(R.string.label_save)) {
                topPadding = dip(10)
                onClick {
                    val selectedSex = if (sex.checkedRadioButtonId == 1) "male" else "female"
                    saveInformation(fullName.text.toString(),
                            interests.text.toString(),
                            age.text.toString(),
                            selectedSex
                            )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (firebase.auth == null) {
            finish()
        }
    }

    /**
     * Return from login if the back button is pressed.
     * MainActivity will force the user to return to onboarding later.
     */
    override fun onBackPressed() {
        setResult(RESULT_OK)
        super.onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == beaconRegisterRequestCode && resultCode == RESULT_OK) {
            setResult(RESULT_OK)
            finish()
        }
    }

    /**
     * Name is a required field for the application.
     */
    fun saveInformation(fullName: String, interests: String, age: String, sex: String) {
        if (fullName.length == 0) {
            Toast.makeText(this, getString(R.string.error_empty_full_name), Toast.LENGTH_LONG)
            return
        }

        PreferenceManager.getDefaultSharedPreferences(this)
            .edit()
            .putString(getString(R.string.settings_full_name), fullName)
            .putString(getString(R.string.settings_interests), interests)
            .putString(getString(R.string.settings_age), age)
            .putString(getString(R.string.settings_sex), sex)
            .apply()

        sendInformationToFirebase(fullName, interests, age, sex)
    }

    /**
     * Create a user profile to Firebase and store information there.
     */
    fun sendInformationToFirebase(fullName: String, interests: String, age: String, sex: String) {
        val userId = PreferenceManager.getDefaultSharedPreferences(this)
                        .getString(getString(R.string.settings_uid), "")

        if (userId.length == 0) {
            Toast.makeText(this, getString(R.string.error_default), Toast.LENGTH_LONG).show()
            return
        }

        // Store the user object
        val userRef = firebase.child(getString(R.string.firebase_users)).child(userId)
        userRef.child(getString(R.string.firebase_users_fullname)).setValue(fullName)
        userRef.child(getString(R.string.firebase_users_interests)).setValue(interests)
        userRef.child(getString(R.string.firebase_users_age)).setValue(age)
        userRef.child(getString(R.string.firebase_users_sex)).setValue(sex)

        moveToBeaconRegistering()
    }

    /**
     * Move to registering a beacon for the user.
     */
    fun moveToBeaconRegistering() {
        val beaconRegisterIntent = Intent(this@OnBoardingActivity, RegisterBeaconActivity::class.java)
        startActivityForResult(beaconRegisterIntent, beaconRegisterRequestCode)
    }
}
