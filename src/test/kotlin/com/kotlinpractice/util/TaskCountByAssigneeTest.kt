package com.kotlinpractice.util

import com.kotlinpractice.model.Task
import com.kotlinpractice.model.TaskPriority
import com.kotlinpractice.model.TaskStatus
import com.kotlinpractice.model.task
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TaskCountByAssigneeTest {
    @Test
    fun `empty input produces an empty report`() {
        assertEquals(emptyMap(), emptyList<Task>().taskCountByAssignee())
    }

    @Test
    fun `counts duplicate assignees and includes only present assignees`() {
        val items = listOf(
            task("one").copy(assigneeId = "alice"),
            task("two").copy(assigneeId = "bob"),
            task("three").copy(assigneeId = "alice")
        )

        assertEquals(mapOf<String?, Int>("alice" to 2, "bob" to 1), items.taskCountByAssignee())
    }

    @Test
    fun `counts unassigned tasks alongside assigned tasks`() {
        val items = listOf(
            task("one"),
            task("two").copy(assigneeId = "alice"),
            task("three")
        )

        assertEquals(mapOf(null to 2, "alice" to 1), items.taskCountByAssignee())
    }

    @Test
    fun `handles entirely unassigned work`() {
        assertEquals(
            mapOf<String?, Int>(null to 2),
            listOf(task("one"), task("two")).taskCountByAssignee()
        )
    }

    @Test
    fun `counts repeated occurrences and all statuses and priorities`() {
        val repeated = task("one").copy(assigneeId = "alice", priority = TaskPriority.LOW)
        val items = listOf(
            repeated,
            repeated,
            task("two", TaskStatus.IN_PROGRESS).copy(assigneeId = "alice"),
            task("three", TaskStatus.BLOCKED).copy(assigneeId = "alice", priority = TaskPriority.HIGH),
            task("four", TaskStatus.DONE).copy(assigneeId = "alice", priority = TaskPriority.CRITICAL)
        )

        assertEquals(mapOf<String?, Int>("alice" to 5), items.taskCountByAssignee())
    }

    @Test
    fun `keeps null empty blank and other assignee IDs distinct`() {
        val items = listOf(
            task("one"),
            task("two").copy(assigneeId = ""),
            task("three").copy(assigneeId = " "),
            task("four").copy(assigneeId = "alice"),
            task("five").copy(assigneeId = " alice ")
        )

        assertEquals(
            mapOf(null to 1, "" to 1, " " to 1, "alice" to 1, " alice " to 1),
            items.taskCountByAssignee()
        )
    }

    @Test
    fun `leaves input unchanged and report is independent of later input changes`() {
        val first = task("one").copy(assigneeId = "alice")
        val second = task("two")
        val items = mutableListOf(first, second)

        val report = items.taskCountByAssignee()

        assertEquals(listOf(first, second), items)
        items.clear()
        items.add(task("three").copy(assigneeId = "bob"))
        assertEquals(mapOf("alice" to 1, null to 1), report)
    }
}
