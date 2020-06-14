package com.example.imageloader

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import com.example.imageloader.Utils.getExternalCacheDir
import com.example.imageloader.Utils.isExternalStorageRemovable
import com.jakewharton.disklrucache.DiskLruCache
import java.io.*


class DiskLruImageCache(
    context: Context, uniqueName: String, diskCacheSize: Int,
    compressFormat: CompressFormat, quality: Int
) {
    private var mDiskCache: DiskLruCache? = null
    private var mCompressFormat = CompressFormat.JPEG
    private var mCompressQuality = 70

    @Throws(IOException::class, FileNotFoundException::class)
    private fun writeBitmapToFile(bitmap: Bitmap?, editor: DiskLruCache.Editor): Boolean {
        var out: OutputStream? = null
        bitmap?.let {
                return try {
                    out = BufferedOutputStream(
                        editor.newOutputStream(0),
                        Utils.IO_BUFFER_SIZE
                    )
                    it.compress(mCompressFormat, mCompressQuality, out)
                } finally {
                    out?.close()
                }
            }?: run {
                return false
            }
    }

    private fun getDiskCacheDir(context: Context, uniqueName: String): File {

        // Check if media is mounted or storage is built-in, if so, try and use external cache dir
        // otherwise use internal cache dir
        val cachePath =
            if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState() ||
                !isExternalStorageRemovable()
            ) getExternalCacheDir(context)?.path else context.cacheDir.path
        return File(cachePath + File.separator.toString() + uniqueName)
    }

    fun put(key: String, data: Bitmap?) {
        var editor: DiskLruCache.Editor? = null
        try {
            editor = mDiskCache?.edit(createKey(key))
            if (editor == null) {
                return
            }
            if (writeBitmapToFile(data, editor)) {
                mDiskCache?.flush()
                editor.commit()
                Utils.logD("cache_test_DISK_", "image put on disk cache $key")

            } else {
                editor.abort()
                Utils.logD("cache_test_DISK_", "ERROR on: image put on disk cache $key")

            }
        } catch (e: IOException) {
            Utils.logD("cache_test_DISK_", "ERROR on: image put on disk cache $key")

            try {
                editor?.abort()
            } catch (ignored: IOException) {
            }
        }
    }

    fun getBitmap(key: String): Bitmap? {
        var bitmap: Bitmap? = null
        var snapshot: DiskLruCache.Snapshot? = null
        try {
            snapshot = mDiskCache?.get(createKey(key))
            if (snapshot == null) {
                return null
            }
            val `in`: InputStream = snapshot.getInputStream(0)
            val buffIn =
                BufferedInputStream(`in`, Utils.IO_BUFFER_SIZE)
            bitmap = BitmapFactory.decodeStream(buffIn)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            snapshot?.close()
        }
        Utils.logD("cache_test_DISK_", if (bitmap == null) "" else "image read from disk $key")
        return bitmap
    }

    fun containsKey(key: String?): Boolean {
        var contained = false
        var snapshot: DiskLruCache.Snapshot? = null
        try {
            snapshot = mDiskCache?.get(createKey(key))
            contained = snapshot != null
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            snapshot?.close()
        }
        return contained
    }

    private fun createKey(key:String?): String {
        return key.hashCode().toString()
    }

    fun clearCache() {
        Utils.logD("cache_test_DISK_", "disk cache CLEARED")
        try {
            mDiskCache?.delete()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    val cacheFolder: File
        get() = mDiskCache?.directory!!

    companion object {
        private const val APP_VERSION = 1
        private const val VALUE_COUNT = 1
    }

    init {
        try {
            val diskCacheDir: File = getDiskCacheDir(context, uniqueName)
            mDiskCache = DiskLruCache.open(
                diskCacheDir,
                APP_VERSION,
                VALUE_COUNT,
                diskCacheSize.toLong()
            )
            mCompressFormat = compressFormat
            mCompressQuality = quality
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}