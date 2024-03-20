package com.example.quevemoshoy.database

import android.app.Application
import android.content.Context

class DBStarter : Application() {
    companion object {
        const val BASE = "MOVIES_DB"
        const val TABLE = "favorite_movies"
        const val VERSION= 1
        lateinit var appContext: Context
        lateinit var DB: DatabaseHelper
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        DB = DatabaseHelper()
    }
}