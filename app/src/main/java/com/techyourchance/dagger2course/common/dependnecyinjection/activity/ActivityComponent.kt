package com.techyourchance.dagger2course.common.dependnecyinjection.activity

import android.app.Application
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.techyourchance.dagger2course.common.dependnecyinjection.app.AppComponent
import com.techyourchance.dagger2course.networking.StackoverflowApi
import com.techyourchance.dagger2course.screens.common.ScreensNavigator
import dagger.Component

@ActivityScope
@Component(dependencies = [AppComponent::class], modules = [ActivityModule::class])
interface ActivityComponent {
    fun activity(): AppCompatActivity
    fun layoutInflater(): LayoutInflater
    fun stackoverflowApi(): StackoverflowApi
    fun fragmentManager(): FragmentManager
    fun screensNavigator(): ScreensNavigator
}
