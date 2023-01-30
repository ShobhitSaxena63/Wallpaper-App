package com.shobhit63.wallpaperworld.data

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.LiveData
import com.shobhit63.wallpaperworld.data.network.ErrorCode
import com.shobhit63.wallpaperworld.data.network.LoadingStatus
import com.shobhit63.wallpaperworld.data.network.PexelsService
import timber.log.Timber
import java.net.UnknownHostException

class HomeRepository(context:Application) {
    private val homeDao:HomeDao = WallpaperDatabase.getDatabase(context).homeDao()
    private val pexelsService by lazy { PexelsService.getInstance() }

    fun getWallpapers():LiveData<List<Wallpapers>>{
        return homeDao.getWallpapers()
    }
//    suspend fun fetchFromNetwork(){
//        val result = pexelsService.getWallpapers()
//        if(result.isSuccessful){
//            val wallpapersList = result.body()
//            wallpapersList?.let { homeDao.insertWallpapers(it.results) }
//        }
//    }
    suspend fun fetchFromNetwork(fetchType: FetchType,search:String = "popular",perPage:String = "20") =
        try{
            deleteAllWallpapers()
            val result = when(fetchType){
                FetchType.Curated -> pexelsService.getWallpapers()
                FetchType.UserSearch -> pexelsService.getSearchWallpapers(search,perPage)
            }
            if(result.isSuccessful) {
                Timber.d("HomeRepository wallpaper list result : $result")
                val wallpaperList = result.body()
                //Logcat needed
                Timber.d("HomeRepository wallpaper list: $wallpaperList")
                wallpaperList?.let {homeDao.insertWallpapers(it.photos) }
                LoadingStatus.success()
            }
            else{
                Timber.d("HomeRepository wallpaper list result : $result")
                LoadingStatus.error(ErrorCode.NO_DATA)
            }
        }catch (ex: UnknownHostException){
            LoadingStatus.error(ErrorCode.NETWORK_ERROR)
        }catch (ex:Exception){
            LoadingStatus.error(ErrorCode.UNKNOWN_ERROR,ex.message)
        }
    suspend fun deleteAllWallpapers(){
        homeDao.deleteAllWallpapers()
    }
}