package com.example.myapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.model.GifData

class ImagesAdapter : RecyclerView.Adapter<ImagesAdapter.ViewHolder>() {
//    init {
//        setHasStableIds(true)
//    }

//    private val imgLoader: ImageLoader = ImageLoader

    var flickrPhotoList: List<GifData> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

//    override fun getItemId(position: Int): Long {
//        return flickrPhotoList[position].id.hashCode()
//    }

    override fun getItemCount(): Int {
        return flickrPhotoList.size
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_photo, viewGroup, false)
        return ViewHolder(v, viewGroup.context)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val flickrPhoto = flickrPhotoList[position]
        viewHolder.setData(flickrPhoto)
    }

    class ViewHolder(itemView: View, private val context: Context) : RecyclerView.ViewHolder(itemView) {

        private var imgPhoto: ImageView = itemView.findViewById(R.id.iv_photo_item)
        private var txtTitle: TextView = itemView.findViewById(R.id.txt_photo_item)

        private var flickrPhoto: GifData? = null

        fun setData(photo: GifData) {
            flickrPhoto = photo

            flickrPhoto?.let {
                Glide
                    .with(context) // replace with 'this' if it's in activity
                    .load(it.imageURL)
                    .asGif()
                    .into(imgPhoto)
                txtTitle.text = it.id
            }
        }
    }
}