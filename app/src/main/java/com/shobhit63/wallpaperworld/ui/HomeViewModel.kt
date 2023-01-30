package com.shobhit63.wallpaperworld.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shobhit63.wallpaperworld.data.FetchType
import com.shobhit63.wallpaperworld.data.HomeRepository
import com.shobhit63.wallpaperworld.data.Wallpapers
import com.shobhit63.wallpaperworld.data.network.LoadingStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(application: Application):AndroidViewModel(application) {
    private val repo:HomeRepository = HomeRepository(application)

    val wallpapers:LiveData<List<Wallpapers>> = repo.getWallpapers()
    private val _loadingStatus = MutableLiveData<LoadingStatus?>()
    val loadingStatus:LiveData<LoadingStatus?>
        get() = _loadingStatus

    fun fetchFromNetwork(fetchType: FetchType,search:String = "popular",perPage:String = "20") {
        _loadingStatus.value = LoadingStatus.loading()
        viewModelScope.launch {
            _loadingStatus.value = withContext(Dispatchers.IO){
//                delay(5000)
                when(fetchType){
                    FetchType.Curated -> repo.fetchFromNetwork(FetchType.Curated)
                    FetchType.UserSearch -> repo.fetchFromNetwork(FetchType.UserSearch,search,perPage)
                    else -> {repo.fetchFromNetwork(FetchType.Curated)}
                }
            }
        }
    }
    fun refreshData(){
        viewModelScope.launch(Dispatchers.IO){
            repo.deleteAllWallpapers()
        }
    }
}