package com.example.agendacorteapp

import android.app.Application
import com.google.firebase.FirebaseApp

class AgendaCorteApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
