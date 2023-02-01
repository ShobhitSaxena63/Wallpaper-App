package com.shobhit63.wallpaperworld.data.network

import com.shobhit63.wallpaperworld.data.Wallpapers

data class PexelsWallpaperList(
    //return response is photos(get from JSON File or pexels documentation)
    val photos: List<Wallpapers>
)