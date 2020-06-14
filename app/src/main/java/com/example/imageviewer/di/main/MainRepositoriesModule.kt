package com.example.imageviewer.di.main

import com.example.imageviewer.ui.repositories.ImageListingRepo
import com.example.imageviewer.ui.repositories.ImageListingRepositoryImpl
import dagger.Binds
import dagger.Module

@Module
abstract class MainRepositoriesModule {

    @Binds
    abstract fun bindsImageListingRepository(imageListingRepositoryImpl: ImageListingRepositoryImpl): ImageListingRepo
}