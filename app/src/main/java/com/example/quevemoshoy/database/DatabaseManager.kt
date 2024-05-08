package com.example.quevemoshoy.database

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.quevemoshoy.model.SimpleMovie

/**
 * DatabaseManager es una clase que maneja las operaciones de la base de datos.
 */
class DatabaseManager {
    /**
     * Crea una nueva película en la base de datos.
     *
     * @param movie La película a crear.
     * @return El ID de la película creada.
     */
    fun create(movie: SimpleMovie): Long {
        val db = DBStarter.DB.writableDatabase
        val values = ContentValues().apply {
            put("id", movie.id)
            put("title", movie.title)
        }
        return db.insertWithOnConflict(
            DBStarter.TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE
        )
    }

    /**
     * Lee todas las películas de la base de datos.
     *
     * @return Una lista de todas las películas.
     */
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

    /**
     * Elimina una película de la base de datos.
     *
     * @param id El ID de la película a eliminar.
     */
    fun delete(id: Int) {
        val db = DBStarter.DB.writableDatabase
        db.delete(DBStarter.TABLE, "id=?", arrayOf(id.toString()))
    }
}