package com.example.project2

import android.os.AsyncTask
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class Async : AsyncTask<Any?, Any?, Any?>() {
    override fun doInBackground(objects: Array<Any?>): Any? {
        try {
            var url: URL = URL("http://google.co.kr")
            var http: HttpURLConnection = url.openConnection() as HttpURLConnection
            println(http.responseCode)
//            println("응답메세지: " + http.responseMessage)
        }catch (e: MalformedURLException) {
            println("http://127.0.0.1:3000 is not a URL I understand");
        }catch (e: IOException) {
            println("응답2")
        }
        return null
    }
}