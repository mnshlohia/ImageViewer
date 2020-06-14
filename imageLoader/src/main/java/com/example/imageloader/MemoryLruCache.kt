package com.example.imageloader

import android.graphics.Bitmap
import android.util.LruCache

class MemoryLruCache {
    private val maxCacheSize: Int = (Runtime.getRuntime().maxMemory() / 1024).toInt() / 8
    private val memoryCache: LruCache<String, Bitmap>
        init {
            memoryCache = object : LruCache<String, Bitmap>(maxCacheSize) {
                override fun sizeOf(key: String, bitmap: Bitmap): Int {
                    return bitmap.byteCount / 1024
                }
            }
        }

    fun put(string: String, bitmap: Bitmap?){
        memoryCache.put(string,bitmap)
    }

    fun get(string: String): Bitmap? {

       return memoryCache.get(string)
    }
}