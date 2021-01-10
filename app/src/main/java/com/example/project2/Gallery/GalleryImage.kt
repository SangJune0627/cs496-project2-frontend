package com.example.project2.Gallery

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64.decode
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import java.util.*
import kotlin.collections.ArrayList

data class GalleryImage (
    var type: Int = -1, // 0 for fd, 1 for bitmap
    var fd: Int? = null,
    var bitmap: Bitmap? = null
) {
    companion object {
        fun parseJson(jsonArray_input: JsonArray): ArrayList<GalleryImage> {
            var returnList = arrayListOf<GalleryImage>()

            for (jsonObject in jsonArray_input) {
                jsonObject as JsonObject
                val imgType = Integer.parseInt(jsonObject["type"].toString())
                if (imgType == 0) { // fd image type
                    val fd = Integer.parseInt(jsonObject["fd"].toString())
                    returnList.add(GalleryImage(type = 0, fd = fd, bitmap = null))
                } else {
                    val imageBytes = decode(jsonObject["bitmap"].toString(), 0)
                    returnList.add(GalleryImage(type = 1, fd = null,
                        bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)))
                }
            }

            return returnList
        }
    }
}