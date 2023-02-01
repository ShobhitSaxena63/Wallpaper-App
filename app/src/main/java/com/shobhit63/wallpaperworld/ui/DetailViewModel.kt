package com.shobhit63.wallpaperworld.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.shobhit63.wallpaperworld.data.DetailRepository
import com.shobhit63.wallpaperworld.data.Wallpapers

class DetailViewModel(application: Application): AndroidViewModel(application) {
    private val repo:DetailRepository = DetailRepository(application)
    val allWallpapers:LiveData<List<Wallpapers>> = repo.getAllWallpaper()
}