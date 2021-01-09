package com.example.project2.Gallery

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project2.SecondFragment
import com.example.project2.Gallery.Frag2_Adapter
import com.example.project2.Gallery.GalleryItem
import com.example.project2.Gallery.SecondFragmentImport
import com.example.project2.MainActivity
import com.example.project2.R
import com.example.project2.Gallery.SecondFragmentZoom

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SecondFragmentGallery.newInstance] factory method to
 * create an instance of this fragment.
 */
class SecondFragmentGallery : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var viewOfLayout: View
    internal lateinit var gv: RecyclerView
    private lateinit var dir_display: TextView

    // for fragment switching
    private lateinit var myContext: FragmentActivity
    private lateinit var fragManager: FragmentManager
    private lateinit var fragTransaction: FragmentTransaction

    // for next fragment
    private lateinit var zoomFragment: SecondFragmentZoom
    private lateinit var selectFragment: SecondFragmentSelect
    private lateinit var importFragment: SecondFragmentImport

    // used for on/off spanCount change
    var isRunning: Boolean = false

    var spanCount: Int = 2

    // initial image resources
    var imgs = arrayListOf<Int>(
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

    // GalleryItem() is similar to inode
    var items: ArrayList<GalleryItem> = ArrayList()
    // current directory string shown on screen
    var dir_current = "root/"

    // parent Gallery class if needed
    var parent: SecondFragmentGallery? = null


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
        // convert img => item
        if (items.size == 0) {
            for (i in imgs) {
                items.add(GalleryItem(0, i, null, null, null))
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        viewOfLayout = inflater.inflate(R.layout.fragment_second_gallery, container, false)

        fragManager = myContext.supportFragmentManager

        gv = viewOfLayout.findViewById(R.id.gridView)

        var adapter = Frag2_Adapter(myContext, items, false, null)
        adapter.setOnItemClickListener { v, pos ->
            when (items[pos].type) {
                1, 3 -> { // child directory
                    items[pos].frag!!.dir_current =
                        dir_current.plus(items[pos].dirName).plus("/")
                    //var i = directories.indexOf(items[position].dirName)
                    fragTransaction = fragManager.beginTransaction()
                    fragTransaction.replace(R.id.secondFragment, items[pos].frag!!)
                    fragTransaction.addToBackStack(null)
                    fragTransaction.commit()
                }
                0, 2 -> { // image file
                    zoomFragment = SecondFragmentZoom()
                    zoomFragment.items = ArrayList(items)
                    zoomFragment.imageIndex = pos

                    fragTransaction = fragManager.beginTransaction()
                    fragTransaction.replace(R.id.secondFragment, zoomFragment)
                    fragTransaction.addToBackStack(null)
                    fragTransaction.commit()

                }
            }
        }

        // Long click for Select mode
        adapter.setOnItemLongClickListener { v, pos ->
            selectFragment = SecondFragmentSelect()
            selectFragment.caller = this
            selectFragment.items = items
            selectFragment.initially_selected = pos
            selectFragment.spanCount = spanCount

            fragTransaction = fragManager.beginTransaction()
            fragTransaction.replace(R.id.secondFragment, selectFragment)
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }
        gv.setAdapter(adapter)

        val gm = GridLayoutManager(requireContext(), spanCount)
        gv.layoutManager = gm

        // for better view
        val spacing: Int = getResources().getDimensionPixelSize(R.dimen.recycler_spacing);
        gv.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                outRect.left = spacing;
                outRect.right = spacing;
                outRect.bottom = spacing;
                outRect.top = spacing;
            }
        })
        gv.setHasFixedSize(true)

        // credit:: by 박해철: begin
        var scaleFactor: Float = -1F
        val gestureDetector = ScaleGestureDetector(requireContext(), object: ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector?): Boolean {
                scaleFactor *= detector!!.scaleFactor
                if (scaleFactor > 1.5F && isRunning) {
                    if (spanCount > 2) {
                        spanCount--
                        scaleFactor = -1F
                        Thread.sleep(10)
                    }
                } else if (scaleFactor > 0F && scaleFactor < 0.7F && isRunning) {
                    if (spanCount < 4) {
                        spanCount++
                        scaleFactor = -1F
                        Thread.sleep(10)
                    }
                }
                gm.spanCount = spanCount
                return super.onScale(detector)
            }
        })

        (activity as MainActivity).registerMyOnTouchListener(object : MainActivity.MyOnTouchListener{
            override fun OnTouch(ev: MotionEvent?) {
                if (ev?.action == MotionEvent.ACTION_DOWN) {
                    scaleFactor = 1F
                }
                gestureDetector.onTouchEvent(ev)
            }
        })
        // credit:: by 박해철: end

        // Button for importing external files from disk
        val importButton = viewOfLayout.findViewById<Button>(R.id.importButton)
        importButton.setOnClickListener {
            importFragment = SecondFragmentImport()
            importFragment.caller = this

            fragTransaction = fragManager.beginTransaction()
            fragTransaction.replace(R.id.secondFragment, importFragment)
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()

        }

        dir_display = viewOfLayout.findViewById(R.id.dir_display)

        return viewOfLayout
    }

    override fun onResume() {
//        Log.d("secondFragmentGallery", "onResume()")
        isRunning = true

        // refresh image and sort directories
        dir_display.text = dir_current
        for (item in items) {
            if (item.type%2 == 1) {
                when (item.frag!!.items.size) {
                    0 -> {
                        item.img = R.drawable.ic_outline_broken_image_24
                        item.type = 1
                    }
                    else -> {
                        item.img = item.frag!!.items[0].img
                        item.bitmap = item.frag!!.items[0].bitmap
                        item.type = if (item.img == null) 3 else 1
                    }
                }
            }
        }
        items.sortWith(compareBy({(1-(it.type%2))},{it.dirName}))
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        isRunning = false
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