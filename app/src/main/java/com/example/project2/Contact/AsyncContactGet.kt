package com.example.project2.Contact

import android.os.AsyncTask
import android.util.Log
import com.example.project2.Repo
import com.example.project2.RetrofitService
import com.example.project2.RetrofitService2
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AsyncContactGet: AsyncTask<Any?, Any?, Any?>() {
    private var result: Repo? = null
    override fun doInBackground(vararg params: Any?): Any? {
        var retrofit = Retrofit.Builder().baseUrl("http://192.249.18.171:4000")
            .addConverterFactory(GsonConverterFactory.create()).build()

        var service1 = retrofit.create(RetrofitService::class.java)

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
        return null
    }
}