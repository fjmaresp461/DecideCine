package com.example.quevemoshoy.database

import android.content.Context

/**
 * DBStarter es una clase que contiene la configuración de la base de datos.
 */
class DBStarter {
    companion object {
        const val TABLE = "favorite_movies"
        const val WATCHED_TABLE = "movies_watched" // Nueva tabla
        const val VERSION = 1
        lateinit var appContext: Context
        lateinit var DB: DatabaseHelper
    }
}