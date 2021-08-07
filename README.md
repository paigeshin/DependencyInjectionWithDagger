# Dagger Tutorial

### Relevant Repository

[https://github.com/paigeshin/PureDependencyInjection](https://github.com/paigeshin/PureDependencyInjection)

# Whole Process for Dagger

1. Create Modules
2. Create Components

   ⇒ Client should never access to `Modules` but through `Components`

# v.0.0.1

### Gradle Configuration

[https://github.com/google/dagger](https://github.com/google/dagger)

- Application Level Gradle

```kotlin
apply plugin: 'com.android.application'
apply plugin: 'kotlin-android'
apply plugin: 'kotlin-android-extensions'
apply plugin: 'kotlin-kapt'

android {
    compileSdkVersion 29
    defaultConfig {
        applicationId "com.techyourchance.dagger2course"
        minSdkVersion 19
        targetSdkVersion 29
        versionCode 1
        versionName "1.0"
        vectorDrawables.useSupportLibrary = true
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }
    compileOptions {
        sourceCompatibility = 1.8
        targetCompatibility = 1.8
    }
}

dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])

    implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlin_version"

    //DI
    api 'com.google.dagger:dagger:$dagger_version'
    kapt 'com.google.dagger:dagger-compiler:$dagger_version'

    implementation 'androidx.appcompat:appcompat:1.1.0'
    implementation 'androidx.recyclerview:recyclerview:1.1.0'
    implementation 'androidx.swiperefreshlayout:swiperefreshlayout:1.0.0'

    implementation 'com.squareup.retrofit2:retrofit:2.6.1'
    implementation 'com.squareup.retrofit2:converter-gson:2.6.1'

    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.7'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.7'
}
```

### Module & Provides

- It should not be `properties` but rather `function`

### Pure Dependency Injection

- PresentationCompositionRoot.kt

```kotlin
class PresentationCompositionRoot(private val activityCompositionRoot: ActivityCompositionRoot) {

    private val layoutInflater get() = activityCompositionRoot.layoutInflater

    private val fragmentManager get() = activityCompositionRoot.fragmentManager

    private val stackoverflowApi get() = activityCompositionRoot.stackoverflowApi

    private val activity get() = activityCompositionRoot.activity

    val screensNavigator get() = activityCompositionRoot.screensNavigator

    val viewMvcFactory get() = ViewMvcFactory(layoutInflater)

    val dialogsNavigator get() = DialogsNavigator(fragmentManager)

    val fetchQuestionsUseCase get() = FetchQuestionsUseCase(stackoverflowApi)

    val fetchQuestionDetailsUseCase get() = FetchQuestionDetailsUseCase(stackoverflowApi)

}
```

### with Dagger

- PresentationModule.kt

```kotlin
@Module
class PresentationModule(private val activityCompositionRoot: ActivityCompositionRoot) {

    @Provides
    fun layoutInflater() = activityCompositionRoot.layoutInflater

    @Provides
    fun fragmentManager() = activityCompositionRoot.fragmentManager

    @Provides
    fun stackoverflowApi() = activityCompositionRoot.stackoverflowApi

    @Provides
    fun activity() = activityCompositionRoot.activity

    @Provides
    fun screenNavigator() = activityCompositionRoot.screensNavigator

    @Provides
    fun viewMvcFactory(layoutInflater: LayoutInflater) = ViewMvcFactory(layoutInflater)

    @Provides
    fun dialogsNavigator(fragmentManager: FragmentManager) = DialogsNavigator(fragmentManager)

    @Provides
    fun fetchQuestionsUseCase(stackoverflowApi: StackoverflowApi) = FetchQuestionsUseCase(stackoverflowApi)

    @Provides
    fun fetchQuestionDetailsUseCase(stackoverflowApi: StackoverflowApi) = FetchQuestionDetailsUseCase(stackoverflowApi)

}
```

### Decide what to expose to clients

- PresentationComponent.kt
- Rule number 1 when using dagger

  ⇒ You should never talk to `Module` directly but through `Components`

```kotlin
@Component(modules = [PresentationModule::class])
interface PresentationComponent {

    fun screenNavigator(): ScreensNavigator

    fun viewMvcFactory(): ViewMvcFactory

    fun dialogsNavigator(): DialogsNavigator

    fun fetchQuestionsUseCase(): FetchQuestionsUseCase

    fun fetchQuestionDetailsUseCase(): FetchQuestionDetailsUseCase

}
```

### Now app looks like

![plot](./image1.png)

### Dagger Conventions

- Components are interfaces annotated with @Component
- Modules are classes annotated with @Module
- Methods in modules that provide services are annotated with @Provides
- Provided services can be used as method in other provider methods

### What I learned from verion 0.0.1

- Dagger are composed of two components
  - Module (@Module, @Provider) => To define what services to be exposed
  - Component (@Compoment(modules = [ModuleContinaer::class])) => To use services
- Instantiation of Dagger
  ```kotlin
    val appComponent: AppComponent by lazy {
      DaggerAppComponent.builder()
        .appModule(AppModule(this))
        .build()
    }
  ```
  - All dagger components properties take shape of function

# V.0.0.2

### Scope in Dagger2

- This service should be instantiated lazily the first time it's required

```kotlin
class ActivityModule(val activity: AppCompatActivity, private val appComponent: AppComponent) {
    private val screensNavigator by lazy {
        ScreensNavigator(activity)
    }
		@Provides
    fun screensNavigator(activity: AppCompatActivity) = ScreensNavigator(activity)
}

```

```kotlin
@Scope
annotation class ActivityScope {

}
class ActivityModule(val activity: AppCompatActivity, private val appComponent: AppComponent) {
		@Provides
		@ActivityScope
    fun screensNavigator(activity: AppCompatActivity) = ScreensNavigator(activity)
}

@ActivityScope
@Component(modules = [ActivityModule::class])
interface ActivityComponent {
    fun activity(): AppCompatActivity
    fun layoutInflater(): LayoutInflater
    fun fragmentManager(): FragmentManager
    fun stackoverflowApi(): StackoverflowApi
    fun screensNavigator(): ScreensNavigator
}
```

⇒ ActivityScope is essentially `Singleton` , but singleton here doesn't mean `Singleton across the whole application`

⇒ All clients get the same instance of a scoped service **from the same instance** of a Component

### Singleton Scope (DefaultScope)

- singleton here doesn't mean `Singleton across the whole application`

- **Before refactoring**

```kotlin
@Module
class AppModule(val application: Application) {

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    private val stackoverflowApi: StackoverflowApi by lazy {
        retrofit.create(StackoverflowApi::class.java)
    }

    @Provides
    fun application() = application

    @Provides
    fun stackoverflowApi() = stackoverflowApi

}
```

- **After refactoring**

```kotlin
@Module
class AppModule(val application: Application) {

    @Provides
    @Singleton
    fun retrofit(): Retrofit {
        return Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    @Provides
    fun application() = application

    // This is not recommended way
    // When working with Dagger2 and you need some dependency, provide it as arguments
    // calling retrofit() can create new instance.. but here we marked retrofit as singleton so it will work even if we put it this way
    // However, recommended way when working with Dagger2, provided needed dependency with `arguments`
    /*
        @Singleton
        @Provides
        fun stackoverflowApi() = retrofit().create(StackoverflowApi::class.java)
     */

    //Correct Way
    @Singleton
    @Provides
    fun stackoverflowApi(retrofit: Retrofit) = retrofit.create(StackoverflowApi::class.java)

}
```

### How to pass desired object in dagger2

- **Wrong Code example**

```kotlin
@Module
class AppModule(val application: Application) {

    @Provides
    @Singleton
    fun retrofit(): Retrofit {
        return Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    @Provides
    fun application() = application

    @Singleton
    @Provides
    fun stackoverflowApi() = retrofit().create(StackoverflowApi::class.java)
}
```

⇒ This is not recommended way

⇒ When working with Dagger2 and need some dependency, provide it as arguments.

⇒ Calling directly retrofit() can create new instance.. but here we marked retrofit as singleton so it will work even if we put it this way

⇒ However, recommended way when working with Dagger2, provided needed dependency with function `arguments`

- **Right way to write dagger code**

```kotlin
@Module
class AppModule(val application: Application) {

    @Provides
    @Singleton
    fun retrofit(): Retrofit {
        return Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    @Provides
    fun application() = application

     //Correct Way
    @Singleton
    @Provides
    fun stackoverflowApi(retrofit: Retrofit) = retrofit.create(StackoverflowApi::class.java)
}
```

### Dagger Conventions (2):

- Scopes are annotations, annotated with @Scope
- Components that provide scoped services must be scoped
- All clients get the same instance of a scoped service **from the same instance** of a Component

# V.0.0.3, Component as Injector

### Refactoring

- Custom Injector
- Injector.kt

⇒ We will remove this.

```kotlin
package com.techyourchance.dagger2course.common.dependnecyinjection

import com.techyourchance.dagger2course.common.dependnecyinjection.presentation.PresentationComponent
import com.techyourchance.dagger2course.questions.FetchQuestionDetailsUseCase
import com.techyourchance.dagger2course.questions.FetchQuestionsUseCase
import com.techyourchance.dagger2course.screens.common.ScreensNavigator
import com.techyourchance.dagger2course.screens.common.dialogs.DialogsNavigator
import com.techyourchance.dagger2course.screens.common.viewsmvc.ViewMvcFactory
import java.lang.reflect.Field

class Injector(private val component: PresentationComponent) {

    fun inject(client: Any) {
        for (field in getAllFields(client)) {
            if (isAnnotatedForInjection(field)) {
                injectField(client, field)
            }
        }
    }

    private fun getAllFields(client: Any): Array<out Field> {
        val clientClass = client::class.java
        return clientClass.declaredFields
    }

    private fun isAnnotatedForInjection(field: Field): Boolean {
        val fieldAnnotations = field.annotations
        for (annotation in fieldAnnotations) {
            if (annotation is Service) {
                return true
            }
        }
        return false
    }

    private fun injectField(client: Any, field: Field) {
        val isAccessibleInitially = field.isAccessible
        field.isAccessible = true
        field.set(client, getServiceForClass(field.type))
        field.isAccessible = isAccessibleInitially
    }

    private fun getServiceForClass(type: Class<*>): Any {
        when (type) {
            DialogsNavigator::class.java -> {
                return component.dialogsNavigator()
            }
            ScreensNavigator::class.java -> {
                return component.screensNavigator()
            }
            FetchQuestionsUseCase::class.java -> {
                return component.fetchQuestionsUseCase()
            }
            FetchQuestionDetailsUseCase::class.java -> {
                return component.fetchQuestionDetailsUseCase()
            }
            ViewMvcFactory::class.java -> {
                return component.viewMvcFactory()
            }
            else -> {
                throw Exception("unsupported service type: $type")
            }
        }
    }

}
```

- PresentationComponent.kt

⇒ Delete all methods and add a new method

```kotlin
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
```

❗️How would my client (QuestionsListFragment here) knows all the necessary dependencies are injected?

- BaseFragment.kt

```kotlin
open class BaseFragment: Fragment() {

    private val presentationComponent: PresentationComponent by lazy {
        DaggerPresentationComponent.builder()
                .presentationModule(PresentationModule((requireActivity() as BaseActivity).activityComponent))
                .build()
    }

//    protected val injector get() = Injector(presentationComponent)
    protected val injector get() = presentationComponent
}
```

- BaseActivty.kt

```kotlin
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
```

- QuestionsListFragment.kt - client

```kotlin
package com.techyourchance.dagger2course.screens.questionslist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.techyourchance.dagger2course.common.dependnecyinjection.Service
import com.techyourchance.dagger2course.questions.FetchQuestionsUseCase
import com.techyourchance.dagger2course.questions.Question
import com.techyourchance.dagger2course.screens.common.ScreensNavigator
import com.techyourchance.dagger2course.screens.common.dialogs.DialogsNavigator
import com.techyourchance.dagger2course.screens.common.fragments.BaseFragment
import com.techyourchance.dagger2course.screens.common.viewsmvc.ViewMvcFactory
import kotlinx.coroutines.*
import javax.inject.Inject

class QuestionsListFragment : BaseFragment(), QuestionsListViewMvc.Listener {

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    @Inject lateinit var fetchQuestionsUseCase: FetchQuestionsUseCase
    @Inject lateinit var dialogsNavigator: DialogsNavigator
    @Inject lateinit var screensNavigator: ScreensNavigator
    @Inject lateinit var viewMvcFactory: ViewMvcFactory

    private lateinit var viewMvc: QuestionsListViewMvc

    private var isDataLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        injector.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewMvc = viewMvcFactory.newQuestionsListViewMvc(container)
        return viewMvc.rootView
    }

    override fun onStart() {
        super.onStart()
        viewMvc.registerListener(this)
        if (!isDataLoaded) {
            fetchQuestions()
        }
    }

    override fun onStop() {
        super.onStop()
        coroutineScope.coroutineContext.cancelChildren()
        viewMvc.unregisterListener(this)
    }

    override fun onRefreshClicked() {
        fetchQuestions()
    }

    private fun fetchQuestions() {
        coroutineScope.launch {
            viewMvc.showProgressIndication()
            try {
                val result = fetchQuestionsUseCase.fetchLatestQuestions()
                when (result) {
                    is FetchQuestionsUseCase.Result.Success -> {
                        viewMvc.bindQuestions(result.questions)
                        isDataLoaded = true
                    }
                    is FetchQuestionsUseCase.Result.Failure -> onFetchFailed()
                }
            } finally {
                viewMvc.hideProgressIndication()
            }
        }
    }

    private fun onFetchFailed() {
        dialogsNavigator.showServerErrorDialog()
    }

    override fun onQuestionClicked(clickedQuestion: Question) {
        screensNavigator.toQuestionDetails(clickedQuestion.id)
    }

}
```

- Dependency Injection Convention

```kotlin
@Component(modules = [PresentationModule::class])
interface PresentationComponent {
    fun inject(fragment: QuestionsListFragment)
    fun inject(activity: QuestionDetailsActivity)
}
```

- Look at these properties in client.

```kotlin
@Inject lateinit var fetchQuestionsUseCase: FetchQuestionsUseCase
@Inject lateinit var dialogsNavigator: DialogsNavigator
@Inject lateinit var screensNavigator: ScreensNavigator
@Inject lateinit var viewMvcFactory: ViewMvcFactory

override fun onCreate(savedInstanceState: Bundle?) {
    injector.inject(this)
    super.onCreate(savedInstanceState)
}
```

⇒ All of these dependencies are automatically injected

### Dependency injection Convention

```kotlin
@Component(modules = [PresentationModule::class])
interface PresentationComponent {
    fun inject(fragment: QuestionsListFragment)
    fun inject(activity: QuestionDetailsActivity)
}
```

❗️How would my client (QuestionsListFragment here) knows all the necessary dependencies are injected?

⇒ This is just dependency injection convention. By declaring interface with @Component, and functions, it automatically injects all the necessary dependencies defined in `Module`

⇒ Here, Dagger auto-generates code

### What is the whole purpose of this?

- Remove Injector
- Our clients is given access to Component directly not through injector

### Before refactoring

![plot](./image2.png)

### After refactoring

![plot](./image3.png)

### Dagger Conventions (3):

- Void methods with single argument defined on components generate injectors for the type of the argument

```kotlin
@Component(modules = [PresentationModule::class])
interface PresentationComponent {
    fun inject(fragment: QuestionsListFragment)
    fun inject(activity: QuestionDetailsActivity)
}
```

- Client's non-private non-final properties (fields) annotated with @Inject designate injection targets

```kotlin
@Inject lateinit var fetchQuestionsUseCase: FetchQuestionsUseCase
@Inject lateinit var dialogsNavigator: DialogsNavigator
@Inject lateinit var screensNavigator: ScreensNavigator
@Inject lateinit var viewMvcFactory: ViewMvcFactory

override fun onCreate(savedInstanceState: Bundle?) {
    injector.inject(this)
    super.onCreate(savedInstanceState)
}
```

# v.0.0.4 - Dependent Components

### Refactoring PresentationModule.kt

- Before refactoring

```kotlin
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
```

- After refactoring

```kotlin
@Module
class PresentationModule() {
    @Provides
    fun viewMvcFactory(layoutInflater: LayoutInflater) = ViewMvcFactory(layoutInflater)
    @Provides
    fun dialogsNavigator(fragmentManager: FragmentManager) = DialogsNavigator(fragmentManager)
    @Provides
    fun fetchQuestionsUseCase(stackoverflowApi: StackoverflowApi) = FetchQuestionsUseCase(stackoverflowApi)
    @Provides
    fun fetchQuestionDetailsUseCase(stackoverflowApi: StackoverflowApi) = FetchQuestionDetailsUseCase(stackoverflowApi)
}
```

⇒ All dependencies come from `ActivityComponent`

### Refactoring PresentationComponent.kt

- Before refactoring

```kotlin
@Component(modules = [PresentationModule::class])
interface PresentationComponent {
    fun inject(fragment: QuestionsListFragment)
    fun inject(activity: QuestionDetailsActivity)
}
```

- After refactoring

```kotlin
@PresentationScope
@Component(dependencies = [ActivityComponent::class], modules = [PresentationModule::class])
interface PresentationComponent {
    fun inject(fragment: QuestionsListFragment)
    fun inject(activity: QuestionDetailsActivity)
}
```

⇒ Component has an explicit dependency declaration of `ActivityComponent::class`

### Inject dependencies

- Before refactoring

```kotlin
DaggerPresentationComponent.builder()
                .presentationModule(PresentationModule(activityComponent))
                .build()
```

- After refactoring

```kotlin
DaggerPresentationComponent.builder()
        .activityComponent(activityComponent)
        .presentationModule(PresentationModule())
        .build()
```

### Dagger Convention (4):

- Component inter-dependencies are specified as part of @Component annotation
- Component B that depends on Component A has implicit access to all services exposed by Component A
  - Services from A can be injected by B
  - Services from A can be consumed inside modules of B
