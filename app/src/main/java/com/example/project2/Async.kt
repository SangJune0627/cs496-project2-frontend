package com.example.project2

import android.os.AsyncTask
import android.util.Log
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class Async : AsyncTask<Any?, Any?, Any?>() {
    override fun doInBackground(objects: Array<Any?>): Any? {
        try {
            val url: URL = URL("http://192.249.18.171:3000")
            val http: HttpURLConnection = url.openConnection() as HttpURLConnection
            println(http.responseCode)
            println(http.responseMessage)
        }catch (e: MalformedURLException) {
            println("http://127.0.0.1:3000 is not a URL I understand");
        }catch (e: IOException) {
            println("응답2"+e.message)
        }
        return null
    }
}