package com.example.imageviewer.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module(includes = [RetrofitModule::class])
class AppModule {

    private var mApplication: Application? = null

    @Provides
    @Singleton
    fun providesApplicationContext(): Context? {
        return mApplication?.applicationContext
    }
}