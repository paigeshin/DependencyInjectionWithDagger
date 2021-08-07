package com.techyourchance.dagger2course.common.dependnecyinjection.presentation

import com.techyourchance.dagger2course.networking.StackoverflowApi
import com.techyourchance.dagger2course.questions.FetchQuestionDetailsUseCase
import com.techyourchance.dagger2course.questions.FetchQuestionsUseCase
import dagger.Module
import dagger.Provides

@Module
class UsecasesModule {
    @Provides
    fun fetchQuestionsUseCase(stackoverflowApi: StackoverflowApi) = FetchQuestionsUseCase(stackoverflowApi)
    @Provides
    fun fetchQuestionDetailsUseCase(stackoverflowApi: StackoverflowApi) = FetchQuestionDetailsUseCase(stackoverflowApi)
}