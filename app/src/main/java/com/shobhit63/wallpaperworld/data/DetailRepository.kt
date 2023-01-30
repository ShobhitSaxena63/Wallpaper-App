package com.shobhit63.wallpaperworld.data

import android.app.Application
import androidx.lifecycle.LiveData

class DetailRepository(context:Application) {
    private val detailDao:DetailDao = WallpaperDatabase.getDatabase(context).detailDao()

    fun getWallpaper(id:Long):LiveData<Wallpapers>{
        return detailDao.getWallpapers(id)
    }
}