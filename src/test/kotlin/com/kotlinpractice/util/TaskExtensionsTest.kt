package com.kotlinpractice.util

import com.kotlinpractice.model.TaskStatus
import com.kotlinpractice.model.TaskPriority
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

    // TODO: activeWork() tests
    @Test
    fun `activeWork should return empty list for empty input`() {
        TODO("Test that activeWork returns empty list when given empty collection")
    }

    @Test
    fun `activeWork should filter out completed tasks`() {
        TODO("Test that only DONE tasks are excluded from activeWork")
    }

    @Test
    fun `activeWork should preserve original order`() {
        TODO("Test that activeWork maintains the original collection order")
    }

    @Test
    fun `activeWork should include all non-DONE statuses`() {
        TODO("Test that activeWork includes TODO, IN_PROGRESS, and BLOCKED tasks")
    }

    // TODO: groupByPriority() tests
    @Test
    fun `groupByPriority should return empty map for empty input`() {
        TODO("Test that groupByPriority returns empty map when given empty collection")
    }

    @Test
    fun `groupByPriority should group tasks by priority level`() {
        TODO("Test that tasks are correctly grouped by TaskPriority (LOW, MEDIUM, HIGH, CRITICAL)")
    }

    @Test
    fun `groupByPriority should preserve order within each priority group`() {
        TODO("Test that the order of tasks is stable within each priority group")
    }

    @Test
    fun `groupByPriority should handle mixed priorities`() {
        TODO("Test groupByPriority with tasks having different priority levels")
    }
}
