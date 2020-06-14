package com.example.imageviewer.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imageviewer.ui.pojos.RedditItem
import com.example.imageviewer.ui.repositories.ImageListingRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class ImageListingViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var imageListingRepositoryImpl: ImageListingRepositoryImpl

    private val liveRedditItemData: MutableLiveData<RedditItem> by lazy {
        MutableLiveData<RedditItem>()
    }

    fun getImageLiveData()=liveRedditItemData



    fun fetchImages() {
        viewModelScope.launch(Dispatchers.Default) {
            imageListingRepositoryImpl.getImages(getImageLiveData())
        }
    }


}