package com.example.imageviewer.di

import com.example.imageviewer.MainActivity
import com.example.imageviewer.SplashActivity
import com.example.imageviewer.di.main.MainActivityModule
import com.example.imageviewer.di.main.MainFragmentBuildersModule
import com.example.imageviewer.di.main.MainRepositoriesModule
import com.example.imageviewer.di.main.MainViewModelsModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuildersModule {


    @ContributesAndroidInjector
    abstract fun contributesSplashActivity(): SplashActivity


    @ContributesAndroidInjector(
        modules = [
            MainActivityModule::class,
            MainFragmentBuildersModule::class,
            MainViewModelsModule::class,
            MainRepositoriesModule::class]
    )
    abstract fun contributesMainActivity(): MainActivity

}