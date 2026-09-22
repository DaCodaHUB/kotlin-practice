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

/** A failed provider, identified by its zero-based position in the input list. */
data class TaskProviderFailure(val providerIndex: Int, val cause: Exception)

/** Successful task data and provider failures from one load. */
data class TaskLoadResult(val tasks: List<Task>, val failures: List<TaskProviderFailure>)

/**
 * Intermediate #1: Load task snapshots from [providers] with explicit failure reporting.
 *
 * On a normally completed load, request each input occurrence's task list exactly once.
 * Return all successful tasks unchanged, in provider-list order and then in each
 * provider's task order. Preserve duplicate tasks and duplicate IDs. An empty provider
 * list returns empty tasks and failures; a provider returning no tasks is successful.
 *
 * A provider exception is recorded with its input index and the original exception
 * instance. Keep failures in input order and retain every successful provider's data,
 * even when other providers fail. If all providers fail, return no tasks and all failures.
 *
 * Cancellation must propagate to the caller, never becoming a reported provider
 * failure or a normally returned result. A cancellation raised by a provider must
 * propagate as the original instance. Cancellation while a provider is suspended
 * must terminate this operation. JVM Errors also propagate unchanged.
 *
 * Do not modify the input or provider-owned task lists. Each call performs a fresh
 * load. Existing TaskRepository calls and observations retain their current behavior.
 * No concurrent-loading requirement is imposed by this exercise.
 */
suspend fun loadTasksWithFailures(providers: List<TaskRepository>): TaskLoadResult =
    TODO("Intermediate #1: Repository error handling")

class InMemoryTaskRepository(initialTasks: Collection<Task> = emptyList()) : TaskRepository {
    private val tasks = MutableStateFlow(initialTasks.distinctBy(Task::id))

    override suspend fun getTasks(): List<Task> = tasks.value

    override suspend fun getTask(id: String): Task? = tasks.value.firstOrNull { it.id == id }

    override fun observeTasks(): Flow<List<Task>> = tasks.asStateFlow()

    fun replaceTasks(updatedTasks: Collection<Task>) {
        tasks.value = updatedTasks.distinctBy(Task::id)
    }
}
