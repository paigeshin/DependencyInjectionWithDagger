package com.techyourchance.dagger2course.common.dependnecyinjection.presentation

import com.techyourchance.dagger2course.screens.questiondetails.QuestionDetailsActivity
import com.techyourchance.dagger2course.screens.questionslist.QuestionsListFragment
import dagger.Component

@Component(modules = [PresentationModule::class])
interface PresentationComponent {
//    fun screensNavigator(): ScreensNavigator
//    fun viewMvcFactory(): ViewMvcFactory
//    fun dialogsNavigator(): DialogsNavigator
//    fun fetchQuestionsUseCase(): FetchQuestionsUseCase
//    fun fetchQuestionDetailsUseCase(): FetchQuestionDetailsUseCase
    fun inject(fragment: QuestionsListFragment)
    fun inject(activity: QuestionDetailsActivity)
}