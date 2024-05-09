package com.pesto.task.management.application

import android.app.Application
import com.google.firebase.FirebaseApp


class PestoNotes : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}