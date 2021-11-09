package com.example.doancn.DI

import android.content.Context
import android.content.SharedPreferences
import com.example.doancn.API.ClassApi.ClassApi
import com.example.doancn.API.ClassApi.SubjectApi
import com.example.doancn.API.IEnrollmentApi
import com.example.doancn.API.IauthApi
import com.example.doancn.Retrofit.Urls
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    @Singleton
    @Provides
    fun provideGsonBuilder(): Gson {
        return GsonBuilder()
            .setPrettyPrinting()
            .excludeFieldsWithoutExposeAnnotation()
            .create()
    }

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit.Builder {
        return Retrofit.Builder()
            .baseUrl(Urls.url1)
            .addConverterFactory(GsonConverterFactory.create())
    }

    @Singleton
    @Provides
    fun provideClassApi(retrofit: Retrofit.Builder): ClassApi {
        return retrofit
            .build()
            .create(ClassApi::class.java)
    }

    @Singleton
    @Provides
    fun provideSubjectApi(retrofit: Retrofit.Builder): SubjectApi {
        return retrofit
            .build()
            .create(SubjectApi::class.java)
    }

    @Singleton
    @Provides

    fun provideAuthApi(retrofit: Retrofit.Builder): IauthApi {
        return retrofit.build().create(IauthApi::class.java)
    }

    @Singleton
    @Provides
    fun provideShareReferences(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences("tokenstorage", Context.MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideEnrollmentApi(retrofit: Retrofit.Builder): IEnrollmentApi {
        return retrofit.build().create(IEnrollmentApi::class.java)

    }


}