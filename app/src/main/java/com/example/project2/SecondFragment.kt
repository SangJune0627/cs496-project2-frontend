package com.example.project2

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Gallery
import android.widget.GridView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.project2.Gallery.GalleryImage
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

    var galleryImages: ArrayList<GalleryImage> = ArrayList()
    var galleryStructure: GalleryStructure = GalleryStructure()

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
        // 처음 galleryImages랑 galleryStructure 초기화
        val imgs = arrayListOf<Int>(
            R.raw.pic_gif,
            R.raw.haring_01,
            R.raw.haring_02,
            R.raw.haring_03,
            R.raw.haring_04,
            R.raw.haring_05,
            R.raw.haring_06,
            R.raw.haring_07,
            R.raw.haring_08,
            R.raw.haring_09,
            R.raw.haring_10,
            R.raw.haring_11,
            R.raw.haring_12,
            R.raw.haring_13,
            R.raw.haring_14,
            R.raw.haring_15,
            R.raw.haring_16,
            R.raw.haring_17,
            R.raw.haring_18,
            R.raw.haring_19,
            R.raw.haring_20,
            R.raw.haring_21,
            R.raw.haring_22,
            R.raw.haring_23,
            R.raw.haring_24
        )
        galleryStructure.dirName = "root"

        imgs.forEachIndexed {index, img_fd ->
            galleryImages.add(GalleryImage(type = 0, fd = img_fd))
            var childStructure = GalleryStructure()
            childStructure.type = 0
            childStructure.imgAddr = index
            galleryStructure.children.add(childStructure)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        viewOfLayout = inflater.inflate(R.layout.fragment_second, container, false)

        fragManager = myContext.supportFragmentManager
        fragTransaction = fragManager.beginTransaction()

        secondFragmentGallery = SecondFragmentGallery()
        secondFragmentGallery.currentStructure = galleryStructure
        secondFragmentGallery.galleryImages = galleryImages

        // initiate secondFragment layout by adding the first gallery fragment
        fragTransaction.add(R.id.secondFragment, secondFragmentGallery)
        fragTransaction.commit()

        return viewOfLayout
    }

    override fun onResume() {
        super.onResume()
        Log.d("SecondFragment", "onResume")
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