package com.example.doancn.DI

import android.content.Context
import com.example.doancn.Utilities.QrCodeManager
import com.example.doancn.Utilities.TokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Named

@Module
@InstallIn(ActivityRetainedComponent::class)
object AppModule {

    @ActivityRetainedScoped
    @Provides
    fun provideQRManager(
        @Named("auth_token") token: String
    ): QrCodeManager {
        return QrCodeManager(token)
    }

    @ActivityRetainedScoped
    @Provides
    @Named("user_role")
    fun provideUserRole(
        @Named("auth_token") token: String
    ): String {
        return TokenManager.getRole(token)
    }

    @ActivityRetainedScoped
    @Provides
    @Named("auth_token")
    fun provideToken(@ApplicationContext context: Context): String {
        return "Bearer " + context.getSharedPreferences("tokenstorage", Context.MODE_PRIVATE)
            .getString("token", null)
    }


}