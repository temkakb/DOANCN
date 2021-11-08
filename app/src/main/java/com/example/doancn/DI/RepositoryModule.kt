package com.example.doancn.DI

import com.example.doancn.API.ClassApi.ClassApi
import com.example.doancn.API.ClassApi.SubjectApi
import com.example.doancn.API.IauthApi
import com.example.doancn.Repository.AuthRepository
import com.example.doancn.Repository.ClassRepository
import com.example.doancn.Repository.SubjectsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideClassRepository(
        classApi: ClassApi
    ): ClassRepository {
        return ClassRepository(classApi)
    }

    @Singleton
    @Provides
    fun provideSubjectsRepository(
        subjectApi: SubjectApi
    ): SubjectsRepository {
        return SubjectsRepository(subjectApi)
    }

    @Singleton
    @Provides
    fun provideAuthRepository(authapi: IauthApi): AuthRepository {
        return AuthRepository(authapi)
    }
}