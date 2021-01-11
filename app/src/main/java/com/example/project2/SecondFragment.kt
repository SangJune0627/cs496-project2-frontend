package com.example.project2

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.project2.Gallery.GalleryImage
import com.example.project2.Gallery.GalleryItem
import com.example.project2.Gallery.GalleryStructure
import com.example.project2.Gallery.SecondFragmentGallery

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SecondFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SecondFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var viewOfLayout: View

    // for fragment switching
    private lateinit var myContext: FragmentActivity
    private lateinit var fragManager: FragmentManager
    private lateinit var fragTransaction: FragmentTransaction

    private lateinit var secondFragmentGallery: SecondFragmentGallery

    lateinit var galleryImages: ArrayList<GalleryImage>
    lateinit var galleryStructure: GalleryStructure

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
    ): View {
        // Inflate the layout for this fragment
        viewOfLayout = inflater.inflate(R.layout.fragment_second, container, false)


//        secondFragmentGallery = SecondFragmentGallery()
//        secondFragmentGallery.currentStructure = galleryStructure
//        secondFragmentGallery.galleryImagesSto = galleryImages

        refresh_Gallery()

//        secondFragmentGallery = buildGallery(galleryStructure, galleryImages, "")
//
//        // initiate secondFragment layout by adding the first gallery fragment
//        fragTransaction.add(R.id.secondFragment, secondFragmentGallery)
//        fragTransaction.commit()

        return viewOfLayout
    }

    override fun onResume() {
        super.onResume()
        Log.d("SecondFragment", "onResume")
    }

    fun refresh_Gallery() {
        fragManager = myContext.supportFragmentManager
        fragTransaction = fragManager.beginTransaction()
        secondFragmentGallery = buildGallery(galleryStructure, galleryImages, "")
        Log.d("buildGallery",secondFragmentGallery.currentStructure.toString())
        fragTransaction.add(R.id.secondFragment, secondFragmentGallery)
        fragTransaction.commit()
    }

    fun buildGallery(structure: GalleryStructure, store: ArrayList<GalleryImage>, dirPath: String): SecondFragmentGallery {
        val debug = structure.children
        Log.d("BuildGallery!", "$debug")
        var galleryBuilt = SecondFragmentGallery()
        galleryBuilt.currentStructure = structure
        galleryBuilt.galleryImagesSto = store
        galleryBuilt.dir_current = galleryBuilt.dir_current + dirPath

        if (structure.children.size != 0) {
            structure.children.forEach {
                var childGallery: SecondFragmentGallery? = null
                if (it.type == 1) {
                    childGallery = buildGallery(it, store, dirPath+it.dirName+"/")
                }
                galleryBuilt.items.add(GalleryItem(it.type, it.imgAddr, it.dirName, childGallery))
            }
        }

        return galleryBuilt
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SecondFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SecondFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}