package com.oliviermarteaux.a056_bricksbreaker.di

import android.app.Application
import com.oliviermarteaux.a056_bricksbreaker.BricksBreakerApplication
import com.oliviermarteaux.shared.firebase.authentication.data.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * This class acts as a Dagger Hilt module, responsible for providing dependencies to other parts of the application.
 * It's installed in the SingletonComponent, ensuring that dependencies provided by this module are created only once
 * and remain available throughout the application's lifecycle.
 */
@Module
@InstallIn(SingletonComponent::class)
class UserModule {

    @Provides
    @Singleton
    fun provideUserRepository(
        application: Application
    ): UserRepository {
        val app = application as BricksBreakerApplication
        return app.bricksBreakerContainer.userRepository
    }
}
