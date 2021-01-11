package com.example.project2.Gallery

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.project2.R

class Frag2_Adapter(val c: Context, val items: ArrayList<GalleryItem>, val canSelect: Boolean, val iSelected: Int?): RecyclerView.Adapter<Frag2_Adapter.Holder>() {
    private val context = c

    var selected: ArrayList<Boolean> = ArrayList<Boolean>()

    private var mListener: OnItemClickListener? = null
    private var mLcListener: OnItemLongClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(v: View, pos: Int)
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(v: View, pos: Int)
    }

    inner class Holder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        val iv = itemView?.findViewById<ImageView>(R.id.imageView)
        val tv = itemView?.findViewById<TextView>(R.id.tv_gallery)

        fun bind(item: GalleryItem, context: Context) {
            Glide.with(context)
                .load(when (item.type) {
                    0, 1 -> item.imgAddr // 이제는 이 어댑터 안쓰니까 일단 아무거나 넣음
                    2, 3 -> item.imgAddr
                    else -> R.drawable.ic_outline_broken_image_24
                })
                .into(iv!!)
            when (item.type) {
                1, 3 -> {tv?.text = item.dirName
                    tv?.setBackgroundResource(R.drawable.gallary_bg) }
                else -> {
                    tv?.text = ""
                    tv?.background = null
                }
            }
        }
    }

    init {
        for (i in 0 until items.size) {
            selected.add(false)
        }
        if (canSelect) {
            if (iSelected != null) {selected[iSelected] = true}
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.row, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(items[position], context)

        if (canSelect) {
            when (selected[position]) {
                false -> holder.itemView.alpha = 1.0F
                true -> holder.itemView.alpha = 0.4F
            }
        }

        holder.itemView.setOnClickListener {
            if (position != RecyclerView.NO_POSITION) {
                if (mListener != null) {
                    mListener!!.onItemClick(holder.itemView, position)
                }
            }
        }
        holder.itemView.setOnLongClickListener {
            if (position != RecyclerView.NO_POSITION) {
                if (mLcListener != null) {
                    mLcListener!!.onItemLongClick(holder.itemView, position)
                }
            }
            return@setOnLongClickListener true
        }
    }

    fun setOnItemClickListener(onItemClick: (v: View, pos: Int)-> Unit) {
        mListener = object: OnItemClickListener {
            override fun onItemClick(v: View, pos: Int) {
                onItemClick(v, pos)
            }
        }
    }

    fun setOnItemLongClickListener(onItemLongClick: (v: View, pos: Int)-> Unit) {
        mLcListener = object: OnItemLongClickListener {
            override fun onItemLongClick(v: View, pos: Int) {
                onItemLongClick(v, pos)
            }
        }
    }
}