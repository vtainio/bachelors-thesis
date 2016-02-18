package com.villetainio.familiarstrangers.activities

import android.os.Bundle
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

class RegisterActivity : AppCompatActivity() {
    val firebase = Firebase(Constants.SERVER_URL)

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
                returnToLogin()
            }

            override fun onError(firebaseError: FirebaseError) {
                // Display error.
                Toast.makeText(applicationContext, firebaseError.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    /**
     * Return to the previous activity with the successful result code.
     */
    fun returnToLogin() {
        setResult(RESULT_OK)
        finish()
    }
}
