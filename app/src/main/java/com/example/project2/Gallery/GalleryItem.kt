package com.example.project2.Gallery

import android.graphics.Bitmap
import com.example.project2.Gallery.SecondFragmentGallery
import com.example.project2.R

data class GalleryItem (
    var type : Int, // 0 for image addr, 1 for directory, -1 for brokenImg
    var imgAddr : Int?,
    var dirName : String?,
    var frag : SecondFragmentGallery?
)

//data class GalleryItem (
//    var type : Int, // 0 for file descriptor, 1 for normal directory, 2 for bitmap file, 3 for directory with bitmap
//    var img : Int?,
//    var bitmap: Bitmap?,
//    var dirName : String?,
//    var frag : SecondFragmentGallery?
//)