package com.example.quevemoshoy.database

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.quevemoshoy.model.SimpleMovie



class DatabaseManager {
    fun create(movie: SimpleMovie): Long {
        val db = DBStarter.DB.writableDatabase
        val values = ContentValues().apply {
            put("id", movie.id)
            put("title", movie.title)
        }
        return db.insertWithOnConflict(
            DBStarter.TABLE,
            null,
            values,
            SQLiteDatabase.CONFLICT_IGNORE
        )
    }

    fun readAll(): MutableList<SimpleMovie> {
        val list = mutableListOf<SimpleMovie>()
        val query = "SELECT * FROM ${DBStarter.TABLE}"
        val db = DBStarter.DB.readableDatabase
        val cursor = db.rawQuery(query, null)
        while (cursor.moveToNext()) {
            val movie = SimpleMovie(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
            )
            list.add(movie)
        }
        cursor.close()
        return list
    }



    fun delete(id: Int) {
        val db = DBStarter.DB.writableDatabase
        db.delete(DBStarter.TABLE, "id=?", arrayOf(id.toString()))
    }







}
