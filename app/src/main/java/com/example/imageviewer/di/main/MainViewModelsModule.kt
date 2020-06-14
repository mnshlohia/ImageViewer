package com.example.imageviewer.di.main


import androidx.lifecycle.ViewModel
import com.example.imageviewer.di.ViewModelKey
import com.example.imageviewer.ui.viewmodels.ImageListingViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MainViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(ImageListingViewModel::class)
    abstract fun bindsImageListingViewModel(homeScreenViewModel: ImageListingViewModel): ViewModel

}