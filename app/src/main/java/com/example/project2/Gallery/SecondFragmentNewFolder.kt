package com.example.project2.Gallery

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.project2.Gallery.GalleryItem
import com.example.project2.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SecondFragmentNewFolder.newInstance] factory method to
 * create an instance of this fragment.
 */
class SecondFragmentNewFolder : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var viewOfLayout: View

    // for passing the spanCount to newly generated gallery
    var spanCount: Int = 2

    lateinit var caller: SecondFragmentGallery
    var items: ArrayList<GalleryItem> = ArrayList<GalleryItem>()

    // for fragment switching
    private lateinit var myContext: FragmentActivity
    private lateinit var fragManager: FragmentManager
    private lateinit var fragTransaction: FragmentTransaction

    private lateinit var folderName: String

    lateinit var currentStructure: GalleryStructure
    lateinit var galleryImagesSto: ArrayList<GalleryImage>

    lateinit var selectedIndices: ArrayList<Int>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myContext = context as FragmentActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewOfLayout = inflater.inflate(R.layout.fragment_second_new_folder, container, false)

        fragManager = myContext.supportFragmentManager

        // generate new image folder by making new SecondFragmentGallery()
        val confirmButton = viewOfLayout.findViewById<Button>(R.id.confirmButton)
        confirmButton.setOnClickListener {
            val inputText = viewOfLayout.findViewById<EditText>(R.id.inputText)
            folderName = inputText.text.toString()
            if (folderName.length == 0) {return@setOnClickListener}
            // generate new gallery and pass some informations
            val newGallery = SecondFragmentGallery()
            newGallery.parent = caller
            newGallery.items = items
            newGallery.spanCount = spanCount
            newGallery.dir_current = caller.dir_current.plus(folderName).plus("/")

            newGallery.galleryImagesSto = galleryImagesSto

            var newStructure = GalleryStructure()
            newStructure.type = 1
            newStructure.dirName = folderName
            items.forEach {
                var imageStructure = GalleryStructure()
                imageStructure.type = 0
                imageStructure.imgAddr = it.imgAddr!!
                newStructure.children.add(imageStructure)
            }
            currentStructure.children.add(0, newStructure)
            caller.items.add(0, GalleryItem(1, items[0].imgAddr, folderName, newGallery))

            newGallery.currentStructure = newStructure

            // remove all the items moved into the new gallery
            for (i in items) {
                caller.items.remove(i)
            }

            for (index in selectedIndices) {
                currentStructure.children.removeAt(index)
            }

            fragTransaction = fragManager.beginTransaction()
            fragTransaction.replace(R.id.secondFragment, newGallery)
            fragTransaction.commit()
        }

        return viewOfLayout
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SecondFragmentNewFolder.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SecondFragmentNewFolder().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}