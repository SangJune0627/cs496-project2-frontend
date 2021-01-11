package com.example.project2.Contact

import android.os.AsyncTask
import android.util.Log
import com.example.project2.Repo
import com.example.project2.RetrofitService
import com.example.project2.RetrofitService2
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import java.util.ArrayList

class AsyncContactGet: AsyncTask<Any?, Any?, Any?>() {
    private var result: ContactRepo? = null

    override fun doInBackground(vararg params: Any?): Any? {
        var retrofit = Retrofit.Builder().baseUrl("http://192.249.18.171:4000/")
            .addConverterFactory(GsonConverterFactory.create()).build()

        var service1 = retrofit.create(ContactRequest::class.java)

        var call1 = service1.getPosts("1234")


        call1.enqueue(object: Callback<ContactRepo> {
            override fun onResponse(call: Call<ContactRepo>, response: Response<ContactRepo>) {
                if (response.isSuccessful) {
                    result = response.body()
                    Log.d("response", "onResponse: 성공, \n" + result.toString())
                } else {
                    Log.d("response", "onResponse: 실패")
                }
            }
            override fun onFailure(call: Call<ContactRepo>, t: Throwable) {
                Log.d("통신실패", "onFailure: " + t.message)
            }
        })
        return null
    }
}

data class ContactRepo(val data: JsonObject)

interface ContactRequest {
    @Headers("Content-Type: application/json")
    @GET("contacts")
    fun getPosts(@Query("owner") owner:String): retrofit2.Call<ContactRepo>
}