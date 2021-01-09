package com.example.project2.Gallery

import android.graphics.Bitmap

data class GalleryImage (
    var type: Int = -1, // 0 for fd, 1 for bitmap
    var fd: Int? = null,
    var bitmap: Bitmap? = null
)