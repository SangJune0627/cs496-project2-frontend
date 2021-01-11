package com.example.project2.Gallery

import com.google.gson.JsonArray
import com.google.gson.JsonObject

// String 의 형식은
// depth!type!img_addr!dirname\n
class GalleryStructure {
    companion object {
        fun parseJson(JsonArray_input: JsonArray): GalleryStructure {
            var root = GalleryStructure()
            root.type = 1
            root.dirName = "root"
            for (jsonObject in JsonArray_input) {
                when ((jsonObject as JsonObject)["type"].toString()) {
                    "0" -> {
                        var child_image = GalleryStructure()
                        child_image.type = 0
                        child_image.imgAddr = Integer.parseInt(jsonObject["imgAddr"].toString())
                        root.children.add(child_image)
                    }
                    else -> { // 1일때
                        var child_dir = parseJson(jsonObject["children"] as JsonArray)
                        child_dir.type = 1
                        child_dir.imgAddr = Integer.parseInt(jsonObject["imgAddr"].toString())
                        child_dir.dirName = jsonObject["dirName"].toString().replace("\"", "")
                        root.children.add(child_dir)
                    }
                }
            }
            return root
        }
    }

    var type: Int = -1 // 0 for image, 1 for directory
    var imgAddr: Int = -1
    var dirName: String = ""
    var children: ArrayList<GalleryStructure> = ArrayList()

    override fun toString(): String { // not working
        var childString = ""
        this.children.forEach{
            childString.plus(it.toString()).plus("\n")
        }
        return "GS(type: $type, imgaddr: $imgAddr, dirname: $dirName, children:\n$childString)"
    }
}