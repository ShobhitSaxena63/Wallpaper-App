package com.shobhit63.wallpaperworld.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query

@Dao
interface DetailDao {
    @Query("SELECT * FROM  wallpapers")
    fun getAllWallpapers(): LiveData<List<Wallpapers>>
}