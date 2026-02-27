package com.oliviermarteaux.a056_bricksbreaker.di

import com.oliviermarteaux.shared.firebase.authentication.data.repository.UserRepository

interface BricksBreakerContainer {
    val userRepository: UserRepository
}