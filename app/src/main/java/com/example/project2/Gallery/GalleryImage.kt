package com.example.project2.Gallery

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64.*
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList

data class GalleryImage (
    var type: Int = -1, // 0 for fd, 1 for bitmap
    var fd: Int? = null,
    var bitmap: Bitmap? = null,
    var bitmapStr: String? = null
) {
    fun initBitmap() {
        if (type == 1) {
            if (bitmap == null) {
                val imageBytes = decode(bitmapStr, 0)
                bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            } else {
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap!!.compress(Bitmap.CompressFormat.PNG, 70, byteArrayOutputStream)
                val imageBytes = byteArrayOutputStream.toByteArray()
                bitmapStr = encodeToString(imageBytes, DEFAULT)
            }
        }
    }

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
                    val imageString =  jsonObject["bitmapStr"].toString()
                    returnList.add(GalleryImage(type = 1, fd = null, bitmap = null,
                        bitmapStr = imageString.substring(1, imageString.length - 2).replace("\\"+"n", "\n")))
                }
            }
            returnList.forEach { it.initBitmap() }
//            println("returnlist")
//            println(returnList)
            return returnList
        }
    }
}