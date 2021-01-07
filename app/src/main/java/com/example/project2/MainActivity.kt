package com.example.project2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var async:Async=Async()
        async.execute()
    }
}