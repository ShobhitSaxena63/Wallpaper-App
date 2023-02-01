package com.shobhit63.wallpaperworld.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

enum class FetchType{
    Curated,UserSearch
}

enum class SetOptions{
    HomeScreen,LockScreen,Both,Save
}

@Entity(tableName = "wallpapers")
data class Wallpapers(
    @PrimaryKey val id:Long,
    val photographer:String,
    val avg_color:String? = null,
    val url:String,
    val src:CustomSources,
    @ColumnInfo(name = "wallpaper_path")
//    @SerializedName("portrait")
    val alt:String
)
