package com.shobhit63.wallpaperworld.data

import android.app.Application
import androidx.lifecycle.LiveData

class DetailRepository(context:Application) {
    private val detailDao:DetailDao = WallpaperDatabase.getDatabase(context).detailDao()

    fun getAllWallpaper():LiveData<List<Wallpapers>>{
        return detailDao.getAllWallpapers()
    }
}