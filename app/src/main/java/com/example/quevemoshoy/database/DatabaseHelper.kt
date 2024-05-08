package com.example.quevemoshoy.database

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * DatabaseHelper es una clase que ayuda a manejar la base de datos.
 *
 * @property userUid El ID del usuario.
 */
class DatabaseHelper(userUid: String) :
    SQLiteOpenHelper(DBStarter.appContext, "${userUid}_DB", null, DBStarter.VERSION) {

    private val createTable =
        "CREATE TABLE ${DBStarter.TABLE}" + "(id INTEGER PRIMARY KEY," + "title TEXT NOT NULL UNIQUE);"

    /**
     * Crea la base de datos.
     *
     * @param db La base de datos a crear.
     */
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(createTable)
    }

    /**
     * Actualiza la base de datos.
     *
     * @param db La base de datos a actualizar.
     * @param oldVersion La versión antigua de la base de datos.
     * @param newVersion La nueva versión de la base de datos.
     */
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropTable = "DROP TABLE IF EXISTS ${DBStarter.TABLE}"
        db?.execSQL(dropTable)
        onCreate(db)
    }
}