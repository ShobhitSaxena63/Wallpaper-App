package com.shobhit63.wallpaperworld.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class BottomDialogViewModelFactory(private val id:Long,private val application: Application):
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BottomDialogViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return BottomDialogViewModel(id,application) as T
        }
        throw java.lang.IllegalArgumentException("UNKNOWN VIEW MODEL CLASS")
    }
}