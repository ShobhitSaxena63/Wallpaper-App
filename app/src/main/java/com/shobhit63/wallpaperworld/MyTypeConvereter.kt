package com.shobhit63.wallpaperworld

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.shobhit63.wallpaperworld.data.CustomSources

class MyTypeConverter {
    @TypeConverter
    fun customSourcesToString(customSource: CustomSources): String = Gson().toJson(customSource)

    @TypeConverter
    fun stringToCustomSources(string: String): CustomSources = Gson().fromJson(string, CustomSources::class.java)
}