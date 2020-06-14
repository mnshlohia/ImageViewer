package com.example.imageviewer.ui.repositories

import androidx.lifecycle.MutableLiveData
import com.example.imageviewer.ui.pojos.RedditItem

interface ImageListingRepo {

    fun getImages(imageLiveData: MutableLiveData<RedditItem>)
}