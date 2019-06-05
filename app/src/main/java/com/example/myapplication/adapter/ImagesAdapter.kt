package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.loader.ImageLoader
import com.example.myapplication.model.FlickrPhoto

class ImagesAdapter() :
    RecyclerView.Adapter<ImagesAdapter.ViewHolder>() {
    private val imgLoader: ImageLoader = ImageLoader()

    var flickrPhotoList: List<FlickrPhoto> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int {
        return flickrPhotoList.size
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_photo, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val flickrPhoto = flickrPhotoList[position]
        viewHolder.setData(flickrPhoto, position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var imgPhoto: ImageView = itemView.findViewById(R.id.iv_photo_item)
        private var txtTitle: TextView = itemView.findViewById(R.id.txt_photo_item)

        private var flickrPhoto: FlickrPhoto? = null

        fun setData(photo: FlickrPhoto, position: Int) {
            flickrPhoto = photo

            flickrPhoto?.let {
                imgPhoto.setImageDrawable(null)
                imgLoader.displayImage(it.imageURL, R.mipmap.ic_launcher, imgPhoto)
                txtTitle.text = it.title
            }
        }
    }
}