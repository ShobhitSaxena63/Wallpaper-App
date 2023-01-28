package com.shobhit63.wallpaperworld.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "wallpapers")
data class Wallpapers(
    @PrimaryKey val id:Long,
    val photographer:String? = null,
    val avg_color:String? = null,
    val url:String,
    val src:CustomSources,
    @ColumnInfo(name = "wallpaper_path")
    @SerializedName("portrait")
    val wallpaperPath:String?=null
)
