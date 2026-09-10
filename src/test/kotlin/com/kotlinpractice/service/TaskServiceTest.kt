package com.kotlinpractice.service

import com.kotlinpractice.cache.InMemoryTaskCache
import com.kotlinpractice.model.TaskStatus
import com.kotlinpractice.model.task
import com.kotlinpractice.repository.InMemoryTaskRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TaskServiceTest {
    @Test
    fun `returns active tasks and excludes completed tasks from observations`() = runTest {
        val repository = InMemoryTaskRepository(listOf(task("open"), task("done", TaskStatus.DONE)))
        val service = TaskService(repository, InMemoryTaskCache())

        assertEquals(listOf("open"), service.getOpenTasks().map { it.id })
        assertEquals(listOf("open"), service.observeOpenTasks().first().map { it.id })
    }

    @Test
    fun `returns null when a task does not exist`() = runTest {
        val service = TaskService(InMemoryTaskRepository(), InMemoryTaskCache())

        assertNull(service.getTask("missing"))
    }
}
