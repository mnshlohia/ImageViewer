package com.example.imageloader

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.os.Handler
import android.widget.ImageView
import java.io.File
import java.util.*
import java.util.Collections.synchronizedMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ImageLoader private constructor(context: Context) {

    private var diskLru: DiskLruImageCache

    private var  memoryLruCache:MemoryLruCache

    private var mCompressFormat = Bitmap.CompressFormat.JPEG
    private var mCompressQuality = 70

    private val executorService: ExecutorService

    private val imageViewMap = synchronizedMap(WeakHashMap<ImageView, String>())
    private val handler: Handler

    init {
        memoryLruCache=MemoryLruCache()

        diskLru = DiskLruImageCache(context, "", 20000, mCompressFormat, mCompressQuality)
        //using fixed thread pool
        executorService = Executors.newFixedThreadPool(5, Utils.ImageThreadFactory())
        /*  ThreadPoolExecutor(4,
            6,
            3000,TimeUnit.SECONDS,
            ArrayBlockingQueue(8),
            Utils.ImageThreadFactory())*/

        handler = Handler()

        val metrics = context.resources.displayMetrics
        screenWidth = metrics.widthPixels
        screenHeight = metrics.heightPixels


    }

    companion object {

        private var INSTANCE: ImageLoader? = null
        private const val APP_VERSION = 1
        private const val VALUE_COUNT = 1
        private const val TAG = "DiskLruImageCache"
        internal var screenWidth = 0
        internal var screenHeight = 0

        @Synchronized
        fun with(context: Context?): ImageLoader {

            require(context != null) {
                "ImageLoader:with - Context should not be null."
            }

            return INSTANCE ?: ImageLoader(context).also {
                INSTANCE = it
            }

        }

        private const val DISK_CACHE_SUBDIR = "thumbnails"
        private const val DISK_CACHE_SIZE = 1024 * 1024 * 10 // 10MB
    }

    private fun getDiskCacheDir(context: Context, uniqueName: String): File {

        // Check if media is mounted or storage is built-in, if so, try and use external cache dir
        // otherwise use internal cache dir
        val cachePath =
            if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState() ||
                !Utils.isExternalStorageRemovable()
            ) Utils.getExternalCacheDir(context)?.path else context.cacheDir.path
        return File(cachePath + File.separator.toString() + uniqueName)
    }

    fun load(imageUrl: String?, imageView: ImageView?) {

        require(imageView != null) {
            "ImageLoader:load - ImageView should not be null."
        }

        require(imageUrl != null && imageUrl.isNotEmpty()) {
            "ImageLoader:load - Image Url should not be empty"
        }

        imageViewMap[imageView] = imageUrl

        var bitmap = checkImageInCache(imageUrl)

        bitmap?.let {
            loadImageIntoImageView(imageView, it, imageUrl)
        } ?: run {
            if (diskLru.containsKey(imageUrl))
                bitmap = diskLru.getBitmap(imageUrl)
            bitmap?.let {
                loadImageIntoImageView(imageView, it, imageUrl)
            } ?: run {
                executorService.submit(PhotosLoader(ImageRequest(imageUrl, imageView)))
            }
        }
    }

    fun load(imageUrl: String?, imageView: ImageView?,callback:(result: String?) -> Unit) {

        require(imageView != null) {
            "ImageLoader:load - ImageView should not be null."
        }

        require(imageUrl != null && imageUrl.isNotEmpty()) {
            "ImageLoader:load - Image Url should not be empty"
        }

        imageViewMap[imageView] = imageUrl

        var bitmap = checkImageInCache(imageUrl)

        bitmap?.let {
            loadImageIntoImageView(imageView, it, imageUrl,callback)
        } ?: run {
            if (diskLru.containsKey(imageUrl))
                bitmap = diskLru.getBitmap(imageUrl)
            bitmap?.let {
                loadImageIntoImageView(imageView, it, imageUrl,callback)
            } ?: run {
                executorService.submit(PhotosLoaderCallback(ImageRequest(imageUrl, imageView),callback))
            }
        }
    }

    @Synchronized
    private fun loadImageIntoImageView(imageView: ImageView, bitmap: Bitmap?, imageUrl: String) {

        require(bitmap != null) {
            "ImageLoader:loadImageIntoImageView - Bitmap should not be null"
        }

        val scaledBitmap = Utils.scaleBitmapForLoad(bitmap, imageView.width, imageView.height)

        scaledBitmap?.let {
            if (!isImageViewReused(ImageRequest(imageUrl, imageView))) imageView.setImageBitmap(
                scaledBitmap
            )
        }
    }


    @Synchronized
    private fun loadImageIntoImageView(
        imageView: ImageView, bitmap: Bitmap?, imageUrl: String,
        callback: (result: String?) -> Unit
    ) {

        require(bitmap != null) {
            "ImageLoader:loadImageIntoImageView - Bitmap should not be null"
        }

        val scaledBitmap = Utils.scaleBitmapForLoad(bitmap, imageView.width, imageView.height)

        scaledBitmap?.let {
            if (!isImageViewReused(ImageRequest(imageUrl, imageView))) imageView.setImageBitmap(
                scaledBitmap
            )
            callback.invoke("success")
        }?:run{
            callback.invoke("failure")
        }

    }


    private fun isImageViewReused(imageRequest: ImageRequest): Boolean {
        val tag = imageViewMap[imageRequest.imageView]
        return tag == null || tag != imageRequest.imgUrl
    }

    @Synchronized
    private fun checkImageInCache(imageUrl: String): Bitmap? = memoryLruCache.get(imageUrl)


    inner class DisplayBitmap(private var imageRequest: ImageRequest) : Runnable {
        override fun run() {
            if (!isImageViewReused(imageRequest)) loadImageIntoImageView(
                imageRequest.imageView,
                checkImageInCache(imageRequest.imgUrl),
                imageRequest.imgUrl
            )
        }
    }

    inner class DisplayBitmapCallback(
        private var imageRequest: ImageRequest,
        private var callback: (result: String?) -> Unit
    ) : Runnable {
        override fun run() {
            if (!isImageViewReused(imageRequest)) loadImageIntoImageView(
                imageRequest.imageView,
                checkImageInCache(imageRequest.imgUrl),
                imageRequest.imgUrl,callback
            )
        }
    }

    inner class ImageRequest(var imgUrl: String, var imageView: ImageView)

    inner class PhotosLoader(private var imageRequest: ImageRequest) : Runnable {

        override fun run() {

            if (isImageViewReused(imageRequest)) return


            val bitmap = Utils.downloadBitmapFromURL(imageRequest.imgUrl)
            //put bitmap in memory cache
            memoryLruCache.put(imageRequest.imgUrl, bitmap)

            //put bitmap in disk cache
            diskLru.put(imageRequest.imgUrl,bitmap)

            if (isImageViewReused(imageRequest)) return

            val displayBitmap = DisplayBitmap(imageRequest)
            handler.post(displayBitmap)
        }
    }


    inner class PhotosLoaderCallback(
        private var imageRequest: ImageRequest,
        private var callback: (result: String?) -> Unit
    ) : Runnable {

        override fun run() {

            if (isImageViewReused(imageRequest)) return


            val bitmap = Utils.downloadBitmapFromURL(imageRequest.imgUrl)
            //put bitmap in memory cache
            memoryLruCache.put(imageRequest.imgUrl, bitmap)

            //put bitmap in disk cache
            diskLru.put(imageRequest.imgUrl,bitmap)

            if (isImageViewReused(imageRequest)) return

            val displayBitmap = DisplayBitmapCallback(imageRequest,callback)
            handler.post(displayBitmap)
        }
    }


}


