package com.example.imageloader

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Environment
import android.os.Process
import android.util.Log
import com.jakewharton.disklrucache.DiskLruCache
import java.io.*
import java.net.URL
import java.util.concurrent.ThreadFactory


object Utils {
    const val IO_BUFFER_SIZE = 8 * 1024

    // Thread Factory to set Thread priority to Display
    internal class ImageThreadFactory : ThreadFactory {
        override fun newThread(runnable: Runnable): Thread {
            return Thread(runnable).apply {
                name = "ImageLoader Thread"
                priority = Process.THREAD_PRIORITY_BACKGROUND
            }
        }
    }

    fun downloadBitmapFromURL(imageUrl: String): Bitmap? {

        val url = URL(imageUrl)
        val inputStream = BufferedInputStream(url.openConnection().getInputStream())

        // Scale Bitmap to Screen Size to store in Cache
        return scaleBitmap(inputStream, ImageLoader.screenWidth, ImageLoader.screenHeight)
    }

    fun scaleBitmapForLoad(bitmap: Bitmap, width: Int, height: Int): Bitmap? {

        if(width == 0 || height == 0) return bitmap

        val stream = ByteArrayOutputStream()
        bitmap.compress(CompressFormat.JPEG, 100, stream)
        val inputStream = BufferedInputStream(ByteArrayInputStream(stream.toByteArray()))
        // Scale Bitmap to required ImageView Size
        return scaleBitmap(inputStream,  width, height)
    }

    /*fun scaleBitmapForLoadDisk(snapshot: DiskLruCache.Snapshot, width: Int, height: Int): Bitmap? {

        if(width == 0 || height == 0) return snapshot
        val `in`: InputStream = snapshot.getInputStream(0)
        val buffIn = BufferedInputStream(ByteArrayInputStream(`in`.toByteArray()))
        snapshot.close()
        return buffIn


        val inputStream = BufferedInputStream(ByteArrayInputStream(stream.toByteArray()))
        // Scale Bitmap to required ImageView Size
        return scaleBitmap(inputStream,  width, height)
    }*/

    private fun scaleBitmap(inputStream: BufferedInputStream, width: Int, height: Int) : Bitmap? {
        return BitmapFactory.Options().run {
            inputStream.mark(inputStream.available())

            inJustDecodeBounds = true
            BitmapFactory.decodeStream(inputStream, null, this)

            inSampleSize = calculateInSampleSize(this, width, height)

            inJustDecodeBounds = false
            inputStream.reset()
            BitmapFactory.decodeStream(inputStream, null,  this)
        }
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {

        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) inSampleSize *= 2
        }

        return inSampleSize
    }

    fun readData(snapshot: DiskLruCache.Snapshot): BufferedInputStream {
        val `in`: InputStream = snapshot.getInputStream(0)
        val buffIn = BufferedInputStream(`in`)
        snapshot.close()
        return buffIn
    }

    fun isExternalStorageRemovable(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            Environment.isExternalStorageRemovable()
        } else true
    }

    fun getExternalCacheDir(context: Context): File? {
        if (hasExternalCacheDir()) {
            return context.externalCacheDir
        }

        // Before Froyo we need to construct the external cache dir ourselves
        val cacheDir =
            "/Android/data/" + context.getPackageName().toString() + "/cache/"
        return File(Environment.getExternalStorageDirectory().getPath().toString() + cacheDir)
    }

    fun hasExternalCacheDir(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO
    }

    fun logD(tag:String,msg:String){
        if (BuildConfig.DEBUG) {
            Log.d("cache_test_DISK_", "disk cache CLEARED")
        }
    }
}
