package com.example.imageviewer.ui.repositories

import androidx.lifecycle.MutableLiveData
import com.example.imageviewer.ui.pojos.RedditItem
import com.example.imageviewer.api.MainApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class ImageListingRepositoryImpl @Inject constructor():ImageListingRepo {

    @Inject
    lateinit var mainApiInterface: MainApiInterface


    override fun getImages(imageLiveData: MutableLiveData<RedditItem>) {
        val callback= object :Callback<RedditItem>{
            override fun onFailure(call: Call<RedditItem>, t: Throwable) {
            }
           override fun onResponse(call: Call<RedditItem>, response: Response<RedditItem>) {
                imageLiveData.value=response.body()
           }
        }
        mainApiInterface.getImages().enqueue(callback)
    }


}