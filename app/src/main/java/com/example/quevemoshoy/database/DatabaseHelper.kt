package com.example.quevemoshoy.database

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class DatabaseHelper(userUid: String) :
    SQLiteOpenHelper(DBStarter.appContext, "${userUid}_DB", null, DBStarter.VERSION) {

    private val createTable = "CREATE TABLE ${DBStarter.TABLE}" +
            "(id INTEGER PRIMARY KEY," +
            "title TEXT NOT NULL UNIQUE);"

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropTable = "DROP TABLE IF EXISTS ${DBStarter.TABLE}"
        db?.execSQL(dropTable)
        onCreate(db)
    }
}

