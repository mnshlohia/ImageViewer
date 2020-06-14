package com.example.imageviewer.di.main

import android.content.Context
import com.example.imageviewer.MainActivity
import com.example.imageviewer.api.MainApiInterface
import com.example.imageviewer.ui.adapters.ImageListingAdapter
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit


@Module
class MainActivityModule {


    private var mainActivity: MainActivity? = null

    var context: Context? = null

    fun MainActivityContextModule(mainActivity: MainActivity?) {
        this.mainActivity = mainActivity
        context = mainActivity
    }


    @Provides
    fun provideContext(): Context? {
        return mainActivity
    }


    @Provides
     fun providesImageListingAdapter(): ImageListingAdapter {
        return ImageListingAdapter()
    }


    @Provides
    fun providesMainApiInterface(retrofit: Retrofit): MainApiInterface {
        return retrofit.create(MainApiInterface::class.java)
    }


}