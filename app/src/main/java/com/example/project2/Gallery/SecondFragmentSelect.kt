package com.example.project2.Gallery

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.GridView
import androidx.core.view.get
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project2.Gallery.Frag2_Adapter
import com.example.project2.Gallery.GalleryItem
import com.example.project2.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SecondFragmentSelect.newInstance] factory method to
 * create an instance of this fragment.
 */
class SecondFragmentSelect : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var viewOfLayout: View
    internal lateinit var gv: RecyclerView

    // for fragment switching
    private lateinit var myContext: FragmentActivity
    private lateinit var fragManager: FragmentManager
    private lateinit var fragTransaction: FragmentTransaction

    // Fragments for next steps
    private lateinit var newFolderFragment: SecondFragmentNewFolder
    private lateinit var setDirDestFragment: SecondFragmentSetDirDest

    // # of columns
    var spanCount: Int = 2

    // Index of an initially selected item
    var initially_selected: Int? = null

    // Indices of selected items
    private var selectedIndices = arrayListOf<Int>()

    // Memorize who have called myself.
    lateinit var caller: SecondFragmentGallery
    // Array of items passed from the caller
    var items: ArrayList<GalleryItem> = ArrayList<GalleryItem>()

    lateinit var galleryImagesSto: ArrayList<GalleryImage>
    lateinit var currentStructure: GalleryStructure


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
        viewOfLayout = inflater.inflate(R.layout.fragment_second_select, container, false)

        fragManager = myContext.supportFragmentManager

        gv = viewOfLayout.findViewById(R.id.selectGridView)

        // Listen which items to choose
        var adapter = Frag2_Adapter_Addr(myContext, items, galleryImagesSto, true, initially_selected)
        adapter.setOnItemClickListener { v, pos ->
            if (selectedIndices.contains(pos)){
                selectedIndices.remove(pos)
                adapter.selected[pos] = false
            } else {
                selectedIndices.add(pos)
                adapter.selected[pos] = true
            }
            adapter.notifyDataSetChanged()
        }
        gv.setAdapter(adapter)

        // Add initial selection to selectedIndices
        if (initially_selected != null) {
            selectedIndices.add(initially_selected!!)
            gv.layoutManager?.scrollToPosition(initially_selected!!)
        }

        val gm = GridLayoutManager(requireContext(), spanCount)
        gv.layoutManager = gm

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

        // Buttons
        val mkdirButton = viewOfLayout.findViewById<Button>(R.id.mkdirButton)
        val copyButton = viewOfLayout.findViewById<Button>(R.id.copyButton)
        val moveButton = viewOfLayout.findViewById<Button>(R.id.moveButton)
        val deleteButton = viewOfLayout.findViewById<Button>(R.id.deleteButton)

        // make a new folder in this directory
        mkdirButton.setOnClickListener{
            if (selectedIndices.size != 0) {
                newFolderFragment = SecondFragmentNewFolder()
                newFolderFragment.items = items.slice(selectedIndices) as ArrayList<GalleryItem>
                newFolderFragment.caller = caller
                newFolderFragment.spanCount = spanCount
                newFolderFragment.galleryImagesSto = galleryImagesSto
                newFolderFragment.currentStructure = currentStructure
                newFolderFragment.selectedIndices = selectedIndices


                fragTransaction = fragManager.beginTransaction()
                fragTransaction.replace(R.id.secondFragment, newFolderFragment)
                fragTransaction.commit()
            }
        }

        // copy selected items to another directory
        copyButton.setOnClickListener{
            if (selectedIndices.size != 0) {
                val newItems = items.slice(selectedIndices) as ArrayList<GalleryItem>
                for (item in newItems) {
                    if (item.type == 1) {
                        Toast.makeText(context, "폴더는 복사할 수 없습니다.", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                }
                setDirDestFragment = SecondFragmentSetDirDest()
                setDirDestFragment.items = newItems
                setDirDestFragment.caller = caller
                setDirDestFragment.copy_mode = true
                setDirDestFragment.selectedIndices = selectedIndices

                setDirDestFragment.galleryImagesSto = galleryImagesSto
                setDirDestFragment.currentStructure = currentStructure

                fragTransaction = fragManager.beginTransaction()
                fragTransaction.replace(R.id.secondFragment, setDirDestFragment)
                fragTransaction.commit()
            }
        }

        // move selected items to another directory
        moveButton.setOnClickListener {
            if (selectedIndices.size != 0) {
                val newItems = items.slice(selectedIndices) as ArrayList<GalleryItem>
                for (item in newItems) {
                    if (item.type == 1) {
                        Toast.makeText(context, "폴더는 이동할 수 없습니다.", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                }
                setDirDestFragment = SecondFragmentSetDirDest()
                setDirDestFragment.items = newItems
                setDirDestFragment.caller = caller
                setDirDestFragment.copy_mode = false
                setDirDestFragment.selectedIndices = selectedIndices

                setDirDestFragment.galleryImagesSto = galleryImagesSto
                setDirDestFragment.currentStructure = currentStructure

                fragTransaction = fragManager.beginTransaction()
                fragTransaction.replace(R.id.secondFragment, setDirDestFragment)
                fragTransaction.commit()
            }
        }

        // delete selected items
        deleteButton.setOnClickListener {
            if (selectedIndices.size != 0) {
                for (i in selectedIndices) {
                    caller.items.removeAt(i)
                    caller.currentStructure.children.removeAt(i)
                }
                fragManager.popBackStack()
            }
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
         * @return A new instance of fragment SecondFragmentSelect.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SecondFragmentSelect().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}