package com.kotlinpractice.util

import com.kotlinpractice.model.TaskStatus
import com.kotlinpractice.model.task
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TaskExtensionsTest {
    @Test
    fun `returns completed tasks and groups unassigned tasks`() {
        val items = listOf(task("open"), task("done", TaskStatus.DONE))

        assertEquals(listOf("done"), items.completed().map { it.id })
        assertEquals(items, items.groupByAssignee()[null])
    }
}
