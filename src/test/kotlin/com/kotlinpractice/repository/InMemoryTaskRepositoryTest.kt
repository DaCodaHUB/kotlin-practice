package com.kotlinpractice.repository

import com.kotlinpractice.model.TaskStatus
import com.kotlinpractice.model.task
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class InMemoryTaskRepositoryTest {
    @Test
    fun `returns tasks by id and exposes the current snapshot`() = runTest {
        val item = task("T-1")
        val repository = InMemoryTaskRepository(listOf(item))

        assertEquals(item, repository.getTask("T-1"))
        assertNull(repository.getTask("missing"))
        assertEquals(listOf(item), repository.observeTasks().first())
    }

    @Test
    fun `keeps the first task for duplicate ids`() = runTest {
        val first = task("T-1", TaskStatus.TODO)
        val repository = InMemoryTaskRepository(listOf(first, task("T-1", TaskStatus.DONE)))

        assertEquals(listOf(first), repository.getTasks())
    }
}
