package com.example.pravki.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.pravki.room.entities.FavoritesEntity

@Dao
interface FavoritesDao {
    @Query("SELECT * FROM FavoritesEntity")
    fun getAll(): LiveData<List<Int>>

    @Query("SELECT * FROM FavoritesEntity WHERE favoriteId = :favoriteId LIMIT 1")
    suspend fun getFavoriteById(favoriteId: Int): Int?

    @Insert
    suspend fun addInFavorites(favoritesEntity: FavoritesEntity)

    @Delete
    suspend fun deleteFromFavorites(favoritesEntity: FavoritesEntity)
}