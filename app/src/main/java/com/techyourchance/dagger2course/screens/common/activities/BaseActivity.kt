package com.techyourchance.dagger2course.screens.common.activities

import androidx.appcompat.app.AppCompatActivity
import com.techyourchance.dagger2course.MyApplication
import com.techyourchance.dagger2course.common.dependnecyinjection.*
import com.techyourchance.dagger2course.common.dependnecyinjection.activity.ActivityModule
import com.techyourchance.dagger2course.common.dependnecyinjection.activity.DaggerActivityComponent
import com.techyourchance.dagger2course.common.dependnecyinjection.presentation.DaggerPresentationComponent
import com.techyourchance.dagger2course.common.dependnecyinjection.presentation.PresentationComponent
import com.techyourchance.dagger2course.common.dependnecyinjection.presentation.PresentationModule

open class BaseActivity: AppCompatActivity() {

    private val appComponent get() = (application as MyApplication).appComponent

    val activityComponent by lazy {
        DaggerActivityComponent.builder()
                .activityModule(ActivityModule(this, appComponent))
                .build()
    }

    private val presentationComponent: PresentationComponent by lazy {
        DaggerPresentationComponent.builder()
                .presentationModule(PresentationModule(activityComponent))
                .build()
    }

//    protected val injector get() = Injector(presentationComponent)
    protected val injector get() = presentationComponent

}