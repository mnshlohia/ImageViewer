package com.example.imageviewer.di

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module

@Module
interface ViewModelFactoryModule {

    @Binds
    fun bindsViewModelFactory(modelProviderFactory: ViewModelProviderFactory):ViewModelProvider.Factory
}