package com.techyourchance.dagger2course.common.dependnecyinjection.activity

import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.techyourchance.dagger2course.common.dependnecyinjection.app.AppComponent
import com.techyourchance.dagger2course.common.dependnecyinjection.app.AppModule
import com.techyourchance.dagger2course.screens.common.ScreensNavigator
import dagger.Module
import dagger.Provides


//class ActivityCompositionRoot(val activity: AppCompatActivity,
//                              private val appCompositionRoot: AppCompositionRoot) {
//    val screensNavigator by lazy {
//        ScreensNavigator(activity)
//    }
//    val application get() = appCompositionRoot.application
//    val layoutInflater: LayoutInflater get() = LayoutInflater.from(activity)
//    val fragmentManager get() = activity.supportFragmentManager
//    val stackoverflowApi get() = appCompositionRoot.stackoverflowApi
//}

@Module
class ActivityModule(val activity: AppCompatActivity, private val appComponent: AppComponent) {
    private val screensNavigator by lazy {
        ScreensNavigator(activity)
    }
    @Provides
    fun activity() = activity
    @Provides
    fun application() = appComponent.application()
    @Provides
    fun screensNavigator(activity: AppCompatActivity) = screensNavigator
    @Provides
    fun layoutInflater() = LayoutInflater.from(activity)
    @Provides
    fun fragmentManager() = activity.supportFragmentManager
    @Provides
    fun stackoverflowApi() = appComponent.stackoverflowApi()
}
