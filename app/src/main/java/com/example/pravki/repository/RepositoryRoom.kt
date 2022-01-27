package com.example.pravki.repository

import androidx.lifecycle.LiveData
import com.example.pravki.room.dao.FavoritesDao
import com.example.pravki.room.entities.FavoritesEntity

class RepositoryRoom(private val favoritesDao: FavoritesDao) {

    val readAllData: LiveData<List<Int>> = favoritesDao.getAll()

    suspend fun getFavoriteById(favoriteId: Int): Int {
        return favoritesDao.getFavoriteById(favoriteId) ?: -5
    }
    suspend fun addInFavorites(favoriteId: Int) {
        favoritesDao.addInFavorites(FavoritesEntity(favoriteId))
    }
    suspend fun deleteFromFavorites(favoriteId: Int) {
        favoritesDao.deleteFromFavorites(FavoritesEntity(favoriteId))
    }
}