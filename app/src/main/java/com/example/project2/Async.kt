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

class Async : AsyncTask<Any?, Any?, Any?>() {
    private var result: Repo? = null

    override fun doInBackground(objects: Array<Any?>): Any? {
//        try {
//            val url: URL = URL("http://192.249.18.171:3000")
//            val http: HttpURLConnection = url.openConnection() as HttpURLConnection
//            val br = BufferedReader(InputStreamReader(http.inputStream))
//            println(http.responseCode)
//            println(http.responseMessage)
//            println(br.readLine())
//        }catch (e: MalformedURLException) {
//            println("http://127.0.0.1:3000 is not a URL I understand");
//        }catch (e: IOException) {
//            println("응답2"+e.message)
//        }

//        try {
//            val url2: URL = URL("http://192.249.18.171:3000")
//            val http2: HttpURLConnection = url2.openConnection() as HttpURLConnection
//
//            http2.defaultUseCaches = false
//            http2.doInput = true
//            http2.doOutput = true
//            http2.requestMethod = "POST"
//
//            var buffer = StringBuffer()
//            buffer.append("yay")
//
//            val outStream = OutputStreamWriter(http2.outputStream, "EUC-KR")
//            val writer = PrintWriter(outStream)
//            writer.write(buffer.toString())
//            writer.flush()
//            Log.d("yay", "finish")
//            println(http2.requestMethod)
//        } catch(e: MalformedURLException) {
//
//        } catch(e: IOException) {
//
//        }

        var retrofit = Retrofit.Builder().baseUrl("http://192.249.18.171:4000")
            .addConverterFactory(GsonConverterFactory.create()).build()

        var retrofit2 = Retrofit.Builder().baseUrl("http://192.249.18.171:4000")
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

//class Repo {
////    @SerializedName으로 일일히 안해주면 아예 이름이 같아야함
//
//    @SerializedName("image_list")
//    lateinit var image_list_JSON: Array<JsonObject>
//
//    @SerializedName("structure")
//    lateinit var structure_JSON: Array<JsonObject>
//
//    override fun toString(): String {
//        return "imagelist\n$image_list_JSON\nstructure\n$structure_JSON"
//    }
//}

//data class Repo(val image_list: Array<JsonObject>, val structure: Array<JsonObject>)
data class Repo(val data: JsonObject)


interface RetrofitService {
    @Headers("Content-Type: application/json")
    @GET("/gallery")
    fun getPosts(): Call<Repo>
}
interface RetrofitService2 {
    @POST("/gallery")
    fun getPosts(@Body structure: GalleryStructure): Call<String>
}