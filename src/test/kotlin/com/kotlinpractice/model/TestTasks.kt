package com.kotlinpractice.model

fun task(id: String, status: TaskStatus = TaskStatus.TODO): Task = Task(
    id = id,
    title = "Task $id",
    description = null,
    status = status,
    priority = TaskPriority.MEDIUM,
    assigneeId = null
)
