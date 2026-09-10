package com.kotlinpractice.model

enum class TaskStatus {
    TODO,
    IN_PROGRESS,
    BLOCKED,
    DONE
}

enum class TaskPriority {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

data class Task(
    val id: String,
    val title: String,
    val description: String?,
    val status: TaskStatus,
    val priority: TaskPriority,
    val assigneeId: String?
)

data class User(
    val id: String,
    val displayName: String,
    val email: String
)

data class Project(
    val id: String,
    val name: String,
    val memberIds: Set<String>
)

// TODO: Implement a safe summary function for Task that:
// - Returns a string containing the task ID and title
// - Includes assignee information only if assigneeId is not null
// - Includes description only if it's not null and not blank
// - Handle the case where description might be an empty string
fun Task.safeSummary(): String {
    TODO("Implement safe summary handling nullable fields")
}
