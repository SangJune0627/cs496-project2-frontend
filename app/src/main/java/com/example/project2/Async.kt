package com.example.project2

import android.os.AsyncTask
import android.util.Log
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*

class Async : AsyncTask<Any?, Any?, Any?>() {
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

        var retrofit = Retrofit.Builder().baseUrl("http://192.249.18.171:3000")
            .addConverterFactory(ScalarsConverterFactory.create()).build()

        var retrofit2 = Retrofit.Builder().baseUrl("http://192.249.18.171:3000")
            .addConverterFactory(GsonConverterFactory.create()).build()

        var service1 = retrofit.create(RetrofitService::class.java)
        var service2 = retrofit2.create(RetrofitService2::class.java)

        var call1 = service1.getPosts("yay")
        var call2 = service2.getPosts("1")

        call1.enqueue(object: Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    Log.d("response", "onResponse: 성공, \n" + result.toString())
                } else {
                    Log.d("respose", "onResponse: 실패")
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d("통신실패", "onFailure: " + t.message)
            }
        })
//
//        Thread.sleep(1000)
//        call2.enqueue(object: Callback<PostResult> {
//            override fun onResponse(call: Call<PostResult>, response: Response<PostResult>) {
//                if (response.isSuccessful) {
//                    val result = response.body()
//                    Log.d("response", "onResponse: 성공, \n" + result.toString())
//                } else {
//                    Log.d("respose", "onResponse: 실패")
//                }
//            }
//
//            override fun onFailure(call: Call<PostResult>, t: Throwable) {
//                Log.d("통신실패asdfasdf", "onFailure: " + t.message)
//            }
//        })

        return null
    }

}

class PostResult {
//    @SerializedName으로 일일히 안해주면 아예 이름이 같아야함

    @SerializedName("userId")
    private var userId: Int = 0

    @SerializedName("id")
    private var id: Int = 0

    @SerializedName("title")
    private lateinit var title: String

    @SerializedName("body")
    private lateinit var body: String

    override fun toString(): String {
        return "PostResult{" +
                "userId=" + userId +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", bodyValue='" + body + '\'' +
                '}'
    }

}



interface RetrofitService {
    @Headers("Content-Type: application/json")
    @POST("test")
    fun getPosts(@Body body: String): Call<String>
}
interface RetrofitService2 {
    @GET("posts/{post}")
    fun getPosts(@Path("post") post: String): Call<PostResult>
}