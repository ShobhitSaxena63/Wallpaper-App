package com.shobhit63.wallpaperworld.data.network



import com.google.gson.Gson
import com.shobhit63.wallpaperworld.BuildConfig
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query


interface PexelsService {
    companion object {
        private const val BASE_URL = "https://api.pexels.com/v1/"


        private val retrofitService by lazy {
            //Add API key to every request
            val interceptor = Interceptor { chain ->
                val request = chain.request()
                val url = request.url().newBuilder()
                    .build()
                val newRequest = request.newBuilder()
                    .url(url)
                    .build()
                chain.proceed(newRequest)
            }
            val httpClient = OkHttpClient().newBuilder().addInterceptor(interceptor).build()

            val gson = GsonBuilder()
                .create()

            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(PexelsService::class.java)
        }

        fun getInstance(): PexelsService {
            return retrofitService
        }
    }

    @Headers("Authorization: "+BuildConfig.PEXELS_API_KEY)
    @GET("search?query=popular&per_page=78")
//    @GET("curated?per_page=78")
    suspend fun getWallpapers(): Response<PexelsWallpaperList>

    @Headers("Authorization: "+BuildConfig.PEXELS_API_KEY)
    @GET("search")
    suspend fun getSearchWallpapers(@Query("query") p1:String, @Query("per_page") p2:String):Response<PexelsWallpaperList>
}
