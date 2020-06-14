package com.example.imageviewer.api

import com.example.imageviewer.ui.pojos.RedditItem
import retrofit2.Call
import retrofit2.http.GET

interface MainApiInterface {

    @GET("r/images/hot.json")
    fun getImages(): Call<RedditItem>
}

