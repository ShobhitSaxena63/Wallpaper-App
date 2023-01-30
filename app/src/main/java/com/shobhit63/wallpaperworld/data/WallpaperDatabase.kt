package com.shobhit63.wallpaperworld.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.shobhit63.wallpaperworld.MyTypeConverter

@TypeConverters(MyTypeConverter::class)
@Database(entities = [Wallpapers::class], version = 1)
abstract class WallpaperDatabase:RoomDatabase() {
    abstract fun homeDao():HomeDao
    abstract fun detailDao():DetailDao

    companion object{
        @Volatile
        private var instance:WallpaperDatabase? = null

        fun getDatabase(context: Context) = instance?: synchronized(this){
            Room.databaseBuilder(context.applicationContext,
                WallpaperDatabase::class.java,
                "wallpapers").build().also { instance = it }
        }
    }
}