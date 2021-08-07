package com.techyourchance.dagger2course.screens.common.activities

import androidx.appcompat.app.AppCompatActivity
import com.techyourchance.dagger2course.MyApplication
import com.techyourchance.dagger2course.common.dependnecyinjection.activity.ActivityModule
import com.techyourchance.dagger2course.common.dependnecyinjection.presentation.PresentationComponent
import com.techyourchance.dagger2course.common.dependnecyinjection.presentation.PresentationModule
import com.techyourchance.dagger2course.common.dependnecyinjection.presentation.UsecasesModule

open class BaseActivity : AppCompatActivity() {

    private val appComponent get() = (application as MyApplication).appComponent

    val activityComponent by lazy {
        appComponent.newActivityComponent(ActivityModule(this))
    }

    private val presentationComponent: PresentationComponent by lazy {
        activityComponent.newPresentationComponent(PresentationModule(), UsecasesModule())
    }

    protected val injector get() = presentationComponent

}