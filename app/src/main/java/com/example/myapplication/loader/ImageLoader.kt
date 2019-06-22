package com.example.myapplication.loader

import android.app.Activity
import android.graphics.Bitmap
import android.util.Log
import android.widget.ImageView
import androidx.annotation.UiThread
import com.example.myapplication.R
import com.example.myapplication.api.ApiManager
import com.example.myapplication.loader.cache.IImageCache
import com.example.myapplication.loader.cache.MemoryImgCache
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

const val THREAD_COUNT = 3

object ImageLoader {

    private val imageViews = Collections.synchronizedMap(WeakHashMap<ImageView, String>()) //ImageView, Image Url
    private val executorService: ExecutorService = Executors.newFixedThreadPool(THREAD_COUNT)
    private val memmoryCache: IImageCache = MemoryImgCache()
    private val apiManager = ApiManager

    private var stubImageId = R.mipmap.ic_launcher

    fun invalidateCache() {
        memmoryCache.invalidate()
    }

    @UiThread
    fun displayImage(url: String, loader: Int, imageView: ImageView) {
        stubImageId = loader
        imageViews[imageView] = url
        queuePhoto(url, imageView)
    }

    @UiThread
    private fun queuePhoto(url: String, imageView: ImageView) {
        val photoToLoad = PhotoToLoad(url, imageView)
        val bitmap = memmoryCache.getCachedBitmap(url)
        if (bitmap != null) {
            val bitmapPresenter = BitmapPresenter(bitmap, photoToLoad)
            bitmapPresenter.run()
        } else if (!imageViewReused(photoToLoad)) {
            executorService.submit(PhotosLoader(photoToLoad))
            imageView.setImageResource(stubImageId)
        }
    }

    //Task for the queue
    data class PhotoToLoad internal constructor(var url: String, var imageView: ImageView)

    private class PhotosLoader(private var photoToLoad: PhotoToLoad) : Runnable {
        override fun run() {
            if (imageViewReused(photoToLoad)) {
                return
            }

            val bmp = apiManager.getBitmap(photoToLoad.url)
            val bitmapPresenter = BitmapPresenter(bmp, photoToLoad)
            if (photoToLoad.imageView.context is Activity) {
                val activity = photoToLoad.imageView.context as Activity
                activity.runOnUiThread(bitmapPresenter)
            }
        }
    }

    private fun imageViewReused(photoToLoad: PhotoToLoad): Boolean {
        val url = imageViews[photoToLoad.imageView]
        return url == null || url != photoToLoad.url
    }

    //Used to display bitmap in the UI thread
    private class BitmapPresenter(private val bitmap: Bitmap?, private var photoToLoad: PhotoToLoad) : Runnable {
        override fun run() {
            if (imageViewReused(photoToLoad)) {
                return
            }

            if (bitmap != null) {
                memmoryCache.add(photoToLoad.url, bitmap)
                photoToLoad.imageView.setImageBitmap(bitmap)
            } else
                photoToLoad.imageView.setImageResource(stubImageId)
        }
    }
}