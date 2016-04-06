package com.villetainio.familiarstrangers.fragments

import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.firebase.client.DataSnapshot
import com.firebase.client.Firebase
import com.firebase.client.FirebaseError
import com.firebase.client.ValueEventListener
import com.villetainio.familiarstrangers.R
import com.villetainio.familiarstrangers.util.Constants

class ProfileFragment : Fragment() {
    val firebase = Firebase(Constants.SERVER_URL)

    /**
     * NewInstance initalization for passing the user ID value.
     */
    companion object {
        fun newInstance(id: String): ProfileFragment {
            var args: Bundle = Bundle()
            args.putString("userId", id)
            var profileFragment: ProfileFragment = newInstance()
            profileFragment.arguments = args
            return profileFragment
        }

        fun newInstance(): ProfileFragment {
            return ProfileFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?) : View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        return view
    }

    override fun onResume() {
        super.onResume()
        fillProfileInformation()
    }

    /**
     * Fetch profile information from Firebase and fill the views accordingly.
     */
    fun fillProfileInformation() {
        val userId = arguments.get("userId") as String
        val userRef = firebase.child(getString(R.string.firebase_users))
            .child(userId)

        userRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val name = view?.findViewById(R.id.profileName) as TextView
                    val interests = view?.findViewById(R.id.profileInterests) as TextView

                    name.text = snapshot.child(getString(R.string.firebase_users_fullname)).value as String
                    interests.text = snapshot.child(getString(R.string.firebase_users_interests)).value as String
                } else {
                    handleServerError()
                }
            }

            override fun onCancelled(error: FirebaseError) {
                handleServerError(error)
            }
        })
    }

    fun handleServerError(error: FirebaseError? = null) {
        //TODO Replace the content with an error layout instead of showing a Toast.
        val errorMessage = if (error == null) getString(R.string.error_default) else error.message
        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
    }
}
