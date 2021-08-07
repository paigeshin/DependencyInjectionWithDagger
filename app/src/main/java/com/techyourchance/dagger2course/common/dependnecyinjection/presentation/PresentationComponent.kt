package com.techyourchance.dagger2course.common.dependnecyinjection.presentation

import com.techyourchance.dagger2course.screens.questiondetails.QuestionDetailsActivity
import com.techyourchance.dagger2course.screens.questionslist.QuestionsListActivity
import com.techyourchance.dagger2course.screens.questionslist.QuestionsListFragment
import dagger.Subcomponent

@PresentationScope
@Subcomponent()
interface PresentationComponent {
    fun inject(fragment: QuestionsListFragment)
    fun inject(activity: QuestionDetailsActivity)
    fun inject(questionsListActivity: QuestionsListActivity)
}
