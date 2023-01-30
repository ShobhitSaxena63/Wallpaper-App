package com.shobhit63.wallpaperworld.ui

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.shobhit63.wallpaperworld.data.DetailRepository
import com.shobhit63.wallpaperworld.data.Wallpapers

class DetailViewModel(id:Long,application: Application):ViewModel() {
    private val repo:DetailRepository = DetailRepository(application)
    val wallpapers:LiveData<Wallpapers> = repo.getWallpaper(id)
}