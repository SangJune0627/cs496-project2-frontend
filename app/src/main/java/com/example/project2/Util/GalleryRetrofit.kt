package com.example.project2.Util

import com.example.project2.Gallery.GalleryImage
import com.example.project2.Gallery.GalleryStructure
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

// for download__________________________________________________________________________

class RetrofitGalleryDownload {
    val url = "http://192.249.18.171:4000/"
    var retrofit_download: Retrofit = Retrofit.Builder().baseUrl(url)
        .addConverterFactory(GsonConverterFactory.create()).build()

    var downloadService = retrofit_download.create(GalleryDownloadService::class.java)
}

interface GalleryDownloadService {
    @GET("gallery/all")
    fun get(@Query("owner") id: String): Call<GalleryBluePrint>
}

data class GalleryBluePrint(val data: JsonObject)

// for upload___________________________________________________________________________

class RetrofitGalleryUpload {
    val url = "http://192.249.18.171:4000/"
    var retrofit_upload: Retrofit = Retrofit.Builder().baseUrl(url)
        .addConverterFactory(GsonConverterFactory.create()).build()

    var uploadService = retrofit_upload.create(GalleryUploadService::class.java)
}

interface GalleryUploadService {
    @POST("gallery/all")
    fun post(@Body imagesAndStructure: Images_and_Structure): Call<GalleryBluePrint>
}

data class Images_and_Structure (val owner: String, val image_list: ArrayList<GalleryImage>, val structure: GalleryStructure)