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
    fun getFavoriteById(favoriteId: Int): Int?

    @Insert
    suspend fun addInFavorites(favoritesEntity: FavoritesEntity)

    @Delete
    suspend fun deleteFromFavorites(favoritesEntity: FavoritesEntity)

    /*
    @Query("SELECT * FROM user")
    fun getAll(): List<User>

    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<User>

    @Query("SELECT * FROM user WHERE first_name LIKE :first AND " +
           "last_name LIKE :last LIMIT 1")
    fun findByName(first: String, last: String): User

    @Insert
    fun insertAll(vararg users: User)

    @Delete
    fun delete(user: User)
    */
}