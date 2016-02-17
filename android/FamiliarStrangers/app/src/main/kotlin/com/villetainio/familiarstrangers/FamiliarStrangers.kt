package com.villetainio.familiarstrangers

import android.app.Application
import com.firebase.client.Firebase

class FamiliarStrangers : Application() {

    override fun onCreate() {
        super.onCreate()
        Firebase.setAndroidContext(this)
    }
}
