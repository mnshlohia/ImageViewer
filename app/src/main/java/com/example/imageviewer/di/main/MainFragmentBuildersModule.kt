package com.example.imageviewer.di.main

import com.example.imageviewer.ui.fragments.ImageListingFragment
import com.example.imageviewer.ui.fragments.ImageViewFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainFragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun  contributesImageListingFragment(): ImageListingFragment

    @ContributesAndroidInjector
    abstract fun  contributesImageViewFragment(): ImageViewFragment


}