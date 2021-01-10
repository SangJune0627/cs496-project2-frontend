package com.example.project2

import android.os.AsyncTask
import android.util.Log
import com.example.project2.Gallery.GalleryStructure
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

class AsynchThirdFragment : AsyncTask<Any?, Any?, Any?>() {
    private var result: Repo? = null

    override fun doInBackground(objects: Array<Any?>): Any? {

        var retrofit = Retrofit.Builder().baseUrl("http://192.249.18.171:3000")
            .addConverterFactory(GsonConverterFactory.create()).build()

        var retrofit2 = Retrofit.Builder().baseUrl("http://192.249.18.171:3000")
            .addConverterFactory(GsonConverterFactory.create()).build()

        var service1 = retrofit.create(RetrofitService::class.java)
        var service2 = retrofit2.create(RetrofitService2::class.java)

        var call1 = service1.getPosts()


        call1.enqueue(object: Callback<Repo> {
            override fun onResponse(call: Call<Repo>, response: Response<Repo>) {
                if (response.isSuccessful) {
                    result = response.body()
                    Log.d("response", "onResponse: 성공, \n" + result.toString())
                } else {
                    Log.d("respose", "onResponse: 실패")
                }
            }
            override fun onFailure(call: Call<Repo>, t: Throwable) {
                Log.d("통신실패", "onFailure: " + t.message)
            }
        })
        while(true) {
            if (this.result != null) {
//                println(GalleryStructure.parseJson(result!!.data["structure"] as JsonArray).toString())
                val structure = GalleryStructure.parseJson(result!!.data["structure"] as JsonArray)
                println(structure.children)
//                println(((result!!.data["image_list"] as JsonArray)[0] as JsonObject)["type"].toString())

                var call2 = service2.getPosts(structure)
                call2.enqueue(object: Callback<String> {
                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        if (response.isSuccessful) {
                            val result = response.body()
                            Log.d("response", "onResponse: 성공, \n" + result.toString())
                        } else {
                            Log.d("respose", "onResponse: 실패")
                        }
                    }

                    override fun onFailure(call: Call<String>, t: Throwable) {
                        Log.d("통신실패asdfasdf", "onFailure: " + t.message)
                    }
                })
                break
            }
        }

        return null
    }

}

interface OmokInterfaceGET {
    @Headers("Content-Type: application/json")
    @GET("/game/omok")
    fun getPosts(): Call<Repo>
}
interface OmokInterfacePOST {
    @POST("/game/omok")
    fun getPosts(@Body structure: GalleryStructure): Call<String>
}