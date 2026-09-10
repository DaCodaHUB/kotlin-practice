package com.kotlinpractice.service

import com.kotlinpractice.cache.TaskCache
import com.kotlinpractice.model.Task
import com.kotlinpractice.model.TaskStatus
import com.kotlinpractice.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskService(
    private val repository: TaskRepository,
    private val cache: TaskCache
) {
    suspend fun getTask(id: String): Task? = cache.get(id) ?: repository.getTask(id)?.also { task ->
        cache.put(task.id, task)
    }

    suspend fun getOpenTasks(): List<Task> = repository.getTasks().filterNot { it.status == TaskStatus.DONE }

    fun observeOpenTasks(): Flow<List<Task>> = repository.observeTasks().map { tasks ->
        tasks.filterNot { it.status == TaskStatus.DONE }
    }

    fun invalidateTask(id: String) {
        cache.remove(id)
    }
}
