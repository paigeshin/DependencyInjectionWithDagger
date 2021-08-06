package com.techyourchance.dagger2course.common.dependnecyinjection.app

import android.app.Application
import com.techyourchance.dagger2course.Constants
import com.techyourchance.dagger2course.networking.StackoverflowApi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

//@UiThread
//class AppCompositionRoot(val application: Application) {
//    private val retrofit: Retrofit by lazy {
//        Retrofit.Builder()
//                .baseUrl(Constants.BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build()
//    }
//    // init stackoverflow API
//    val stackoverflowApi: StackoverflowApi by lazy {
//        retrofit.create(StackoverflowApi::class.java)
//    }
//}

@Module
class AppModule(val application: Application) {

    @Provides
    @AppScope
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
    @Provides
    @AppScope
    fun stackoverflowApi(retrofit: Retrofit) = retrofit.create(StackoverflowApi::class.java)

}

//@Module
//class AppModule(val application: Application) {
//
//    private val retrofit: Retrofit by lazy {
//        Retrofit.Builder()
//                .baseUrl(Constants.BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build()
//    }
//
//    private val stackoverflowApi: StackoverflowApi by lazy {
//        retrofit.create(StackoverflowApi::class.java)
//    }
//
//    @Provides
//    fun application() = application
//
//    @Provides
//    fun stackoverflowApi() = stackoverflowApi
//
//}
//
