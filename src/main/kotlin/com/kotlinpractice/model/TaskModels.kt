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
