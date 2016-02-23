package com.villetainio.familiarstrangers.activities

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.firebase.client.Firebase
import org.jetbrains.anko.*
import com.villetainio.familiarstrangers.R
import com.villetainio.familiarstrangers.util.Constants

class OnBoardingActivity : AppCompatActivity() {
    val firebase = Firebase(Constants.SERVER_URL)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        verticalLayout {
            val information = textView()
            val fullNameLabel = textView(getString(R.string.label_full_name))
            val fullName = editText()
            val interestsLabel = textView(getString(R.string.label_interests))
            val interests = editText()
            button(getString(R.string.label_save)) {
                onClick {
                    saveInformation(fullName.text.toString(), interests.text.toString())
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

    /**
     * Name is a required field for the application.
     */
    fun saveInformation(fullName: String, interests: String) {
        if (fullName.length == 0) {
            Toast.makeText(this, getString(R.string.error_empty_full_name), Toast.LENGTH_LONG)
            return
        }

        PreferenceManager.getDefaultSharedPreferences(this)
            .edit()
            .putString(getString(R.string.settings_full_name), fullName)
            .putString(getString(R.string.settings_interests), interests)
            .apply()

        sendInformationToFirebase(fullName, interests)
    }

    /**
     * Create a user profile to Firebase and store information there.
     */
    fun sendInformationToFirebase(fullName: String, interests: String) {
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

        finishOnBoarding()
    }

    /**
     * Finish onboarding successfully
     */
    fun finishOnBoarding() {
        setResult(RESULT_OK)
        finish()
    }
}
