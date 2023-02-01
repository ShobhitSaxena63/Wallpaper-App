package com.shobhit63.wallpaperworld.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query

@Dao
interface BottomDialogDao {
    @Query("SELECT * FROM  wallpapers where `id`=:id")
    fun getWallpapers(id:Long): LiveData<Wallpapers>
}