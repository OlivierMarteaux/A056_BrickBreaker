package com.oliviermarteaux.a056_bricksbreaker.data

import com.oliviermarteaux.a056_bricksbreaker.domain.User

class UserRepository {
    private val users = mutableListOf(
        User("1", "alice@test.com", "Alice", 120),
        User("2", "bob@test.com", "Bob", 150),
        User("3", "charlie@test.com", "Charlie", 90)
    )
    
    private var currentUser = User("0", "me@test.com", "Player1", 0)
    
    fun updateScore(newScore: Long) {
        // Only update if it's a better score (lower time), or just update it
        if (currentUser.score == 0L || newScore < currentUser.score) {
            currentUser = currentUser.copy(score = newScore)
            val index = users.indexOfFirst { it.id == currentUser.id }
            if (index != -1) {
                users[index] = currentUser
            } else {
                users.add(currentUser)
            }
        }
    }
    
    fun getAllScores(): List<User> {
        return users.sortedBy { it.score }
    }
    
    fun setCurrentUser(user: User) {
        currentUser = user
    }
}
