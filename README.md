# ImageViewer

This is a Image Loading and viewer app using Reddit Api to load images and view them.

It uses memory cache and disk cache to load images if images are already loaded earlier.

Used LruCache for Memory caching and Jake Whalton's Disk Cache for Disk Cache in ImageLoader module.

Loading Images: 

1. Check Memory cache first.
2. If image found in memory cache, loads image from memory cache.
3. If not found in memory cache, checks for image in disk cache and loads image if found.
4. if image not found in either cache, put the image in both the caches and load the image.


Also, apart from that have used MVVM, Dagger2, Retrofit2, Coroutines , Jetpack Paging and Jetpack Navigation Components.

