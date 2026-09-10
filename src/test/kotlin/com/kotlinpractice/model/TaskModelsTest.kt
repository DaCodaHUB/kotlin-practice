package com.kotlinpractice.model

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TaskModelsTest {
    @Test
    fun `tasks use value equality`() {
        assertEquals(task("T-1"), task("T-1"))
    }

    @Test
    fun `safe summary with null assignee`() {
        val task = task("T-1")
        val summary = task.safeSummary()
        assertEquals("T-1: Task T-1", summary)
    }

    @Test
    fun `safe summary with null description`() {
        val task = task("T-1").copy(description = null)
        val summary = task.safeSummary()
        assertEquals("T-1: Task T-1", summary)
    }

    @Test
    fun `safe summary with blank description`() {
        val task = task("T-1").copy(description = "")
        val summary = task.safeSummary()
        assertEquals("T-1: Task T-1", summary)
    }

    @Test
    fun `safe summary with populated assignee and description`() {
        val task = task("T-1").copy(
            assigneeId = "USER-123",
            description = "Important task"
        )
        val summary = task.safeSummary()
        assertEquals("T-1: Task T-1 (Assignee: USER-123, Description: Important task)", summary)
    }
}
