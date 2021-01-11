package com.example.project2.Contact

import com.google.gson.JsonArray

class ContactStructure {
    companion object {
//        fun parseJson(JsonArray_input: JsonArray): ContactStructure {
//
//            return
//        }
    }

    var type: Int = -1 // 0 for image, 1 for directory
    var imgAddr: Int = -1
    var dirName: String = ""
    var children: ArrayList<ContactStructure> = ArrayList()

    override fun toString(): String { // not working
        var childString = ""
        this.children.forEach{
            childString.plus(it.toString()).plus("\n")
        }
        return "GS(type: $type, imgaddr: $imgAddr, dirname: $dirName, children:\n$childString)"
    }
}