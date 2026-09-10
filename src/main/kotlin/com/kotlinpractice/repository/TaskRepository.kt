package com.kotlinpractice.repository

import com.kotlinpractice.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

interface TaskRepository {
    suspend fun getTasks(): List<Task>
    suspend fun getTask(id: String): Task?
    fun observeTasks(): Flow<List<Task>>
}

class InMemoryTaskRepository(initialTasks: Collection<Task> = emptyList()) : TaskRepository {
    private val tasks = MutableStateFlow(initialTasks.distinctBy(Task::id))

    override suspend fun getTasks(): List<Task> = tasks.value

    override suspend fun getTask(id: String): Task? = tasks.value.firstOrNull { it.id == id }

    override fun observeTasks(): Flow<List<Task>> = tasks.asStateFlow()

    fun replaceTasks(updatedTasks: Collection<Task>) {
        tasks.value = updatedTasks.distinctBy(Task::id)
    }
}
