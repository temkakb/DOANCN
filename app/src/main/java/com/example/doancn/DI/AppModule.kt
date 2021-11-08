package com.example.doancn.DI

import android.content.SharedPreferences
import com.example.doancn.Utilities.QrCodeManager
import com.example.doancn.Utilities.TokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Named

@Module
@InstallIn(ActivityRetainedComponent::class)
object AppModule {

    @ActivityRetainedScoped
    @Provides
    fun provideQRManager(
        @Named("auth_token") token: String?
    ): QrCodeManager {
        return QrCodeManager(token!!)
    }

    @ActivityRetainedScoped
    @Provides
    @Named("user_role")
    fun provideUserRole(
        @Named("auth_token") token: String?
    ): String? {
        return if (token != null) TokenManager.getRole(token) else null
    }

    @ActivityRetainedScoped
    @Provides
    @Named("auth_token")
    fun provideToken(sharedPreferences: SharedPreferences): String? {
        if (sharedPreferences.contains("token")) {
            return "Bearer " + sharedPreferences.getString("token", null)
        }
        return null
    }
}