package com.oliviermarteaux.a056_bricksbreaker.di

import android.content.Context
import com.oliviermarteaux.shared.firebase.authentication.data.repository.UserFirebaseRepository
import com.oliviermarteaux.shared.firebase.authentication.data.repository.UserRepository
import com.oliviermarteaux.shared.firebase.authentication.data.service.UserApi
import com.oliviermarteaux.shared.firebase.authentication.data.service.UserFirebaseApi

class BricksBreakerAppContainer(context: Context)
    : BricksBreakerContainer {
    private val userApi: UserApi = UserFirebaseApi(context)

    override val userRepository: UserRepository by lazy {
        UserFirebaseRepository(userApi)
    }
}