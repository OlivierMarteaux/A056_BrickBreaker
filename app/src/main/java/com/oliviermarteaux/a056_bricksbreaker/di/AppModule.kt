package com.oliviermarteaux.a056_bricksbreaker.di

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import com.oliviermarteaux.shared.utils.AndroidLogger
import com.oliviermarteaux.shared.utils.CoroutineDispatcherProvider
import com.oliviermarteaux.shared.utils.Logger
import com.oliviermarteaux.shared.utils.checkInternetConnection
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import java.util.prefs.Preferences
import javax.inject.Singleton

/**
 * This class acts as a Dagger Hilt module, responsible for providing dependencies to other parts of the application.
 * It's installed in the SingletonComponent, ensuring that dependencies provided by this module are created only once
 * and remain available throughout the application's lifecycle.
 */
@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideLogger(): Logger = AndroidLogger

    @Provides
    fun provideIsOnlineFlow(
        @ApplicationContext context: Context
    ): Flow<Boolean> = checkInternetConnection(context)

    @Provides
    @Singleton
    fun provideCoroutineDispatcherProvider(): CoroutineDispatcherProvider {
        return CoroutineDispatcherProvider()
    }
}
