package com.kotlinpractice.util

import com.kotlinpractice.model.TaskPriority
import com.kotlinpractice.model.TaskStatus
import com.kotlinpractice.model.task
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TaskDisplayLabelTest {
    @Test
    fun `formats a typical label with a description`() {
        val item = task("review", TaskStatus.IN_PROGRESS).copy(
            title = "Review pull request",
            priority = TaskPriority.HIGH,
            description = "Check the tests"
        )

        assertEquals(
            "[HIGH] Review pull request (IN_PROGRESS) - Check the tests",
            item.displayLabel()
        )
    }

    @Test
    fun `formats every priority and status combination`() {
        val priorities = listOf(
            TaskPriority.LOW to "LOW",
            TaskPriority.MEDIUM to "MEDIUM",
            TaskPriority.HIGH to "HIGH",
            TaskPriority.CRITICAL to "CRITICAL"
        )
        val statuses = listOf(
            TaskStatus.TODO to "TODO",
            TaskStatus.IN_PROGRESS to "IN_PROGRESS",
            TaskStatus.BLOCKED to "BLOCKED",
            TaskStatus.DONE to "DONE"
        )

        for ((priority, priorityText) in priorities) {
            for ((status, statusText) in statuses) {
                val item = task("sample", status).copy(title = "Sample", priority = priority)
                assertEquals("[$priorityText] Sample ($statusText)", item.displayLabel())
            }
        }
    }

    @Test
    fun `omits null empty and whitespace-only descriptions`() {
        for (description in listOf(null, "", " ", "\t\n\r ")) {
            val item = task("sample").copy(title = "Sample", description = description)
            assertEquals("[MEDIUM] Sample (TODO)", item.displayLabel())
        }
    }

    @Test
    fun `preserves title and nonblank description text`() {
        val item = task("sample").copy(title = "  Sample  ", description = "  Details  ")
        assertEquals("[MEDIUM]   Sample   (TODO) -   Details  ", item.displayLabel())
    }

    @Test
    fun `supports empty and blank titles`() {
        assertEquals("[MEDIUM]  (TODO)", task("sample").copy(title = "").displayLabel())
        assertEquals("[MEDIUM]    (TODO)", task("sample").copy(title = "  ").displayLabel())
    }

    @Test
    fun `excludes id and assignee and leaves task unchanged`() {
        val item = task("internal-id").copy(title = "Sample", assigneeId = "internal-user")
        val original = item.copy()

        assertEquals("[MEDIUM] Sample (TODO)", item.displayLabel())
        assertEquals(original, item)
    }
}
