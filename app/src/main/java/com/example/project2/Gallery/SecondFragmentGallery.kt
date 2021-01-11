package com.example.project2.Gallery

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
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
import com.example.project2.MainActivity
import com.example.project2.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

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

    // GalleryItem() is similar to inode
    var items: ArrayList<GalleryItem> = ArrayList()
    // current directory string shown on screen
    var dir_current = "root/"

    // 자기 아래에 있는 구조를 담고있다. 이게 SecondFragment에 있는 Structure와 계속 교류할 수 있을지는 의문.
    lateinit var currentStructure: GalleryStructure

    // 이미지 리소스 포인터
    lateinit var galleryImagesSto: ArrayList<GalleryImage>

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
//        // convert img => item
//        if (items.size == 0) {
//            imgs.forEachIndexed{ index, img ->
//                items.add(GalleryItem(type = 0, imgAddr = index, dirName = null, frag = null))
//            }
//        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        viewOfLayout = inflater.inflate(R.layout.fragment_second_gallery, container, false)

        fragManager = myContext.supportFragmentManager

        gv = viewOfLayout.findViewById(R.id.gridView)

        var adapter_addr = Frag2_Adapter_Addr(myContext, items, galleryImagesSto, false, null)

//        var adapter = Frag2_Adapter(myContext, items, false, null) // 삭제되어야 함

        adapter_addr.setOnItemClickListener { v, pos ->
            when (items[pos].type) {
                1 -> { // child directory

                    fragTransaction = fragManager.beginTransaction()
                    fragTransaction.replace(R.id.secondFragment, items[pos].frag!!)
                    fragTransaction.addToBackStack(null)
                    fragTransaction.commit()
                }
                0 -> { // image file
                    zoomFragment = SecondFragmentZoom()
                    zoomFragment.items = ArrayList(items)
                    zoomFragment.imageIndex = pos
                    zoomFragment.galleryImagesSto = galleryImagesSto

                    fragTransaction = fragManager.beginTransaction()
                    fragTransaction.replace(R.id.secondFragment, zoomFragment)
                    fragTransaction.addToBackStack(null)
                    fragTransaction.commit()

                }
            }
        }

        // Long click for Select mode
        adapter_addr.setOnItemLongClickListener { v, pos ->
            selectFragment = SecondFragmentSelect()
            selectFragment.caller = this
            selectFragment.items = items
            selectFragment.galleryImagesSto = galleryImagesSto
            selectFragment.initially_selected = pos
            selectFragment.spanCount = spanCount
            selectFragment.currentStructure = currentStructure

            fragTransaction = fragManager.beginTransaction()
            fragTransaction.replace(R.id.secondFragment, selectFragment)
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }

//        gv.setAdapter(adapter)
        gv.adapter = adapter_addr

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
            importFragment.currentStructure = currentStructure
            importFragment.galleryImagesSto = galleryImagesSto

            fragTransaction = fragManager.beginTransaction()
            fragTransaction.replace(R.id.secondFragment, importFragment)
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()

        }

        val cloudButton = viewOfLayout.findViewById<FloatingActionButton>(R.id.bt_cloud_gallery)
        cloudButton.setOnClickListener{
            var builder : AlertDialog.Builder= AlertDialog.Builder(context)
            builder.setTitle("Cloud Synchronization").setMessage("Load or Save?")
            builder.setNegativeButton("Pull", object: DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface, which:Int) {
                    //TODO: 화이팅
                }
            })
            builder.setPositiveButton("Push", object: DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface, which:Int) {
                    downloadContacts()
                }
            })
            builder.show()
        }

        dir_display = viewOfLayout.findViewById(R.id.dir_display)

        return viewOfLayout
    }

    override fun onResume() {
        Log.d("secondFragmentGallery", "onResume()")
        isRunning = true

        // refresh image and sort directories
        dir_display.text = dir_current
        for (item in items) {
            if (item.type%2 == 1) {
                when (item.frag!!.items.size) {
                    0 -> {
                        item.type = -1
                    }
                    else -> {
                        item.imgAddr = item.frag!!.items[0].imgAddr
                        item.type = 1
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