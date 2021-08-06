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
