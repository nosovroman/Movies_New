package com.example.pravki.room.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.pravki.common.Constants
import com.example.pravki.room.dao.FavoritesDao
import com.example.pravki.room.entities.FavoritesEntity

@Database(entities = [FavoritesEntity::class], version = 1)
abstract class DatabaseFavorites : RoomDatabase() {
    abstract fun favoritesDao(): FavoritesDao

    companion object {
        private var INSTANCE: DatabaseFavorites? = null
        fun getInstance(context: Context): DatabaseFavorites {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        DatabaseFavorites::class.java,
                        Constants.ROOM_DB_NAME
                    ).fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}