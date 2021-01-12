package com.example.project2.Util

import com.example.project2.Omok.User
import com.google.gson.JsonArray
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
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

// for making a game room____________________________________________________________________

class RetrofitGameMakeRoom {
    val url = "http://192.249.18.171:4000/"
    var retrofit_make_room: Retrofit = Retrofit.Builder().baseUrl(url)
        .addConverterFactory(GsonConverterFactory.create()).build()

    var makeRoomService = retrofit_make_room.create(GameMakeRoomService::class.java)
}

interface GameMakeRoomService {
    @POST("game/makeroom")
    fun post(@Body user: User): Call<GameRoomBluePrint>
}

// for waiting an opponent ____________________________________________________________________

class RetrofitGameWaitOpponent {
    val url = "http://192.249.18.171:4000/"
    var retrofit_wait_opponent: Retrofit = Retrofit.Builder().baseUrl(url)
        .addConverterFactory(GsonConverterFactory.create()).build()

    var waitOpponentService = retrofit_wait_opponent.create(GameWaitOpponentService::class.java)
}

interface GameWaitOpponentService {
    @GET("game/wait")
    fun get(@Query("roomnumber") roomNumber: String): Call<GameRoomBluePrint>
}

// for being an opponent _____________________________________________________________________

class RetrofitGameBeOpponent {
    val url = "http://192.249.18.171:4000/"
    var retrofit_be_opponent: Retrofit = Retrofit.Builder().baseUrl(url)
        .addConverterFactory(GsonConverterFactory.create()).build()

    var beOpponentService = retrofit_be_opponent.create(GameBeOpponentService::class.java)
}

interface GameBeOpponentService {
    @POST("game/enterroom")
    fun post(@Body challenge: Challenge): Call<GameRoomBluePrint>
}

data class Challenge(val roomnumber: String, val id: String, val name: String)

// for waiting a move ________________________________________________________________________

// for sending a move ________________________________________________________________________

class RetrofitGameSendMove {
    val url = "http://192.249.18.171:4000/"
    var retrofit_send_move: Retrofit = Retrofit.Builder().baseUrl(url)
        .addConverterFactory(GsonConverterFactory.create()).build()

    var beOpponentService = retrofit_send_move.create(GameBeOpponentService::class.java)
}

interface GameSendMoveService {

}



data class Move(val id: String, val name: String, val roomNumber: String, val coordinates: Coordinates)

data class Coordinates(val x: Int, val y: Int)