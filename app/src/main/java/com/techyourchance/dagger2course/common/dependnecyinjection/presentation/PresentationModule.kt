package com.techyourchance.dagger2course.common.dependnecyinjection.presentation

import android.view.LayoutInflater
import androidx.fragment.app.FragmentManager
import com.techyourchance.dagger2course.common.dependnecyinjection.activity.ActivityComponent
import com.techyourchance.dagger2course.networking.StackoverflowApi
import com.techyourchance.dagger2course.questions.FetchQuestionDetailsUseCase
import com.techyourchance.dagger2course.questions.FetchQuestionsUseCase
import com.techyourchance.dagger2course.screens.common.dialogs.DialogsNavigator
import com.techyourchance.dagger2course.screens.common.viewsmvc.ViewMvcFactory
import dagger.Module
import dagger.Provides

//class PresentationCompositionRoot(val activityCompositionRoot: ActivityCompositionRoot) {
//    private val layoutInflater get() = activityCompositionRoot.layoutInflater
//    private val fragmentManager get() = activityCompositionRoot.fragmentManager
//    private val stackoverflowApi get() = activityCompositionRoot.stackoverflowApi
//    private val activity get() = activityCompositionRoot.activity
//    val screensNavigator get() = activityCompositionRoot.screensNavigator
//    val viewMvcFactory get() = ViewMvcFactory(layoutInflater)
//    val dialogsNavigator get() = DialogsNavigator(fragmentManager)
//
//    // Object should be stateful
//    // When you want to share a object and multiple clients try to access it, that might be harmful.
//    // However, if you put get(), this getter function will be invoked and will create a new instance of which questions to use.
//    // `Just make sure you put `getter` when you want to make some object sharable between multiple clients.`
//    val fetchQuestionsUseCase get() = FetchQuestionsUseCase(stackoverflowApi)
//    val fetchQuestionDetailUseCase get() = FetchQuestionDetailsUseCase(stackoverflowApi)
//}

@Module
class PresentationModule(private val activityComponent: ActivityComponent) {
    @Provides
    fun layoutInflater() = activityComponent.layoutInflater()
    @Provides
    fun fragmentManager() = activityComponent.fragmentManager()
    @Provides
    fun stackoverflowApi() = activityComponent.stackoverflowApi()
    @Provides
    fun activity() = activityComponent.activity()
    //실제 사용.
    @Provides
    fun screensNavigator() = activityComponent.screensNavigator()
    @Provides
    fun viewMvcFactory(layoutInflater: LayoutInflater) = ViewMvcFactory(layoutInflater)
    @Provides
    fun dialogsNavigator(fragmentManager: FragmentManager) = DialogsNavigator(fragmentManager)
    @Provides
    fun fetchQuestionsUseCase(stackoverflowApi: StackoverflowApi) = FetchQuestionsUseCase(stackoverflowApi)
    @Provides
    fun fetchQuestionDetailsUseCase(stackoverflowApi: StackoverflowApi) = FetchQuestionDetailsUseCase(stackoverflowApi)
}