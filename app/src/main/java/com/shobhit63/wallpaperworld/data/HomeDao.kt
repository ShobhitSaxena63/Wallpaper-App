package com.shobhit63.wallpaperworld.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface HomeDao {
    @Query("SELECT * FROM  wallpapers")
    fun getWallpapers():LiveData<List<Wallpapers>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWallpapers(wallpapers: List<Wallpapers>)

    @Query("DELETE FROM wallpapers")
    suspend fun deleteAllWallpapers()
}