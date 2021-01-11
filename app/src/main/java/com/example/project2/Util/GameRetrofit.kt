package com.example.project2.Util

import com.google.gson.JsonArray
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Query

// for downloading game rooms __________________________________________________________________________

class RetrofitGameRoomDownload {
    val url = "http://192.249.18.171:4000/"
    var retrofit_download: Retrofit = Retrofit.Builder().baseUrl(url)
        .addConverterFactory(GsonConverterFactory.create()).build()

    var downloadService = retrofit_download.create(GameRoomDownloadService::class.java)
}

interface GameRoomDownloadService {
    @GET("game/room")
    fun get(@Query("id") id: String, @Query("name") name: String): Call<GameRoomBluePrint>
}

data class GameRoomBluePrint(val data: JsonArray)

// for uploading game room request ____________________________________________________________________