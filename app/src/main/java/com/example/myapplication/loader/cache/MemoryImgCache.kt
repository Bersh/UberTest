package com.example.myapplication.loader.cache

import android.graphics.Bitmap
import android.util.LruCache

class MemoryImgCache(override val size: Int = 70 * 1024 * 1024 /* 70MB */) : IImageCache {
    private val cache = object : LruCache<String, Bitmap>(size) {
        override fun sizeOf(key: String, value: Bitmap): Int {
            return value.byteCount
        }
    }

    override fun isImageInCache(url: String): Boolean {
        synchronized(cache) {
            return cache[url] != null
        }
    }

    override fun getCachedBitmap(url: String): Bitmap? {
        synchronized(cache) {
            return cache[url]
        }
    }

    override fun add(url: String, bitmap: Bitmap) {
        synchronized(cache) {
            if (cache[url] == null || cache[url] != bitmap) {
                cache.put(url, bitmap)
            }
        }
    }

    override fun invalidate() {
        synchronized(cache) {
            cache.evictAll()
        }
    }
}