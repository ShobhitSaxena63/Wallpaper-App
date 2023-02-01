package com.shobhit63.wallpaperworld.ui

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.shobhit63.wallpaperworld.data.BottomDialogRepository
import com.shobhit63.wallpaperworld.data.Wallpapers

class BottomDialogViewModel(id:Long,application: Application): ViewModel() {
    private val repo: BottomDialogRepository = BottomDialogRepository(application)
    val wallpapers: LiveData<Wallpapers> = repo.getWallpaper(id)
}