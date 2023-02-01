package com.shobhit63.wallpaperworld.data

import android.app.Application
import androidx.lifecycle.LiveData

class BottomDialogRepository(context: Application) {
    private val bottomDialogDao:BottomDialogDao = WallpaperDatabase.getDatabase(context).bottomDialogDao()

    fun getWallpaper(id:Long): LiveData<Wallpapers> {
        return bottomDialogDao.getWallpapers(id)
    }
}