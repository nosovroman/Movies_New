package com.example.pravki.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FavoritesEntity(
    @PrimaryKey val favoriteId: Int
)
