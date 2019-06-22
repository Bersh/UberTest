package com.example.myapplication.loader.cache

import android.graphics.Bitmap

interface IImageCache {
    val size: Int // in bytes
    fun isImageInCache(url: String): Boolean
    fun getCachedBitmap(url: String): Bitmap?
    fun add(url: String, bitmap: Bitmap)
    fun invalidate()
}