package com.example.myapplication.loader

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import com.example.myapplication.R
import com.example.myapplication.TIMEOUT
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

const val THREAD_COUNT = 5

class ImageLoader {

    private val imageViews = Collections.synchronizedMap(WeakHashMap<ImageView, String>())
    private val executorService: ExecutorService = Executors.newFixedThreadPool(THREAD_COUNT)

    private var stubImageId = R.mipmap.ic_launcher

    fun displayImage(url: String, loader: Int, imageView: ImageView) {
        stubImageId = loader
        imageViews[imageView] = url
        queuePhoto(url, imageView)
        imageView.setImageResource(loader)
    }

    private fun queuePhoto(url: String, imageView: ImageView) {
        val p = PhotoToLoad(url, imageView)
        executorService.submit(PhotosLoader(p))
    }

    private fun getBitmap(url: String): Bitmap? {

        val imageUrl = URL(url)
        val conn = imageUrl.openConnection() as HttpURLConnection
        conn.requestMethod = "GET"
        val bmOptions = BitmapFactory.Options()
        bmOptions.inSampleSize = 1
        conn.connectTimeout = TIMEOUT
        conn.readTimeout = TIMEOUT
        conn.instanceFollowRedirects = true
        conn.doInput = true
        conn.connect()
        try {
            return BitmapFactory.decodeStream(conn.inputStream, null, bmOptions)
        } finally {
            conn.inputStream.close()
        }
    }

    //Task for the queue
    inner class PhotoToLoad internal constructor(var url: String, var imageView: ImageView)

    inner class PhotosLoader(var photoToLoad: PhotoToLoad) : Runnable {

        override fun run() {
            if (imageViewReused(photoToLoad))
                return
            val bmp = getBitmap(photoToLoad.url)
            val bd = BitmapPresenter(bmp, photoToLoad)
            val a = photoToLoad.imageView.context as Activity
            a.runOnUiThread(bd)
        }
    }

    private fun imageViewReused(photoToLoad: PhotoToLoad): Boolean {
        val tag = imageViews[photoToLoad.imageView]
        return tag == null || tag != photoToLoad.url
    }

    //Used to display bitmap in the UI thread
    internal inner class BitmapPresenter(var bitmap: Bitmap?, var photoToLoad: PhotoToLoad) : Runnable {
        override fun run() {
            if (imageViewReused(photoToLoad))
                return
            if (bitmap != null)
                photoToLoad.imageView.setImageBitmap(bitmap)
            else
                photoToLoad.imageView.setImageResource(stubImageId)
        }
    }
}