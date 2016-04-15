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

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.firebase.client.*
import com.villetainio.familiarstrangers.util.Constants
import com.villetainio.familiarstrangers.R
import org.jetbrains.anko.onClick

class ChatActivity : AppCompatActivity() {
    val firebase = Firebase(Constants.SERVER_URL)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val userId = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getString(R.string.settings_uid), "")
        val strangerId = intent.extras.getString("strangerId")


        val ownEncounterRef = firebase.child(getString(R.string.firebase_users))
            .child(userId)
            .child(getString(R.string.firebase_users_encounters))
            .child(strangerId)

        ownEncounterRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists() && snapshot.child("chatId").exists()) {
                    initializeChat(snapshot.child("chatId").value as String)

                } else {
                    val strangerEncounterRef = firebase.child(getString(R.string.firebase_users))
                            .child(strangerId)
                            .child(getString(R.string.firebase_users_encounters))
                            .child(userId)

                    strangerEncounterRef.addListenerForSingleValueEvent(object: ValueEventListener {

                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists() && snapshot.child("chatId").exists()) {
                                initializeChat(snapshot.child("chatId").value as String)

                            } else {
                                // Neither of them had a chat id so let's create one.
                                val chatRef = firebase.child(getString(R.string.firebase_chat)).push()
                                ownEncounterRef.child("chatId").setValue(chatRef.key)
                                strangerEncounterRef.child("chatId").setValue(chatRef.key)

                                initializeChat(chatRef.key)
                            }
                        }

                        override fun onCancelled(error: FirebaseError) {
                            handleServerError(error)
                        }
                    })


                }
            }

            override fun onCancelled(error: FirebaseError) {
                handleServerError(error)
            }
        })
    }

    /**
     * Handle the chat connection between Firebase and the application.
     */
    fun initializeChat(chatId: String) {
        val chatRef = firebase.child(getString(R.string.firebase_chat))
            .child(chatId)

        // Read existing chat messages.
        chatRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //TODO Implement.
            }

            override fun onCancelled(error: FirebaseError) {
                handleServerError(error)
            }
        })

        // Listen for new chat objects.
        chatRef.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildKey: String) {
                //TODO Add chat objects to view.
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildKey: String) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildKye: String) {}

            override fun onCancelled(error: FirebaseError) {
                handleServerError(error)
            }
        })

        // Send a new message when clicking the send button.
        val sendMessage = findViewById(R.id.chatSend) as Button
        sendMessage.onClick { view -> run {
            val messageView = findViewById(R.id.chatMessage) as EditText
            val message = messageView.text

            //TODO Send chat messages to Firebase.
        }}
    }

    fun handleServerError(error: FirebaseError? = null) {
        val errorMessage = if (error == null) getString(R.string.error_default) else error.message
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    }
}
