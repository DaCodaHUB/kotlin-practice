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
        val items = emptyList<com.kotlinpractice.model.Task>()
        assertEquals(emptyList(), items.activeWork())
    }

    @Test
    fun `activeWork should filter out completed tasks`() {
        val items = listOf(
            task("open", TaskStatus.TODO),
            task("done", TaskStatus.DONE),
            task("in-progress", TaskStatus.IN_PROGRESS)
        )
        val active = items.activeWork()
        assertEquals(2, active.size)
        assertEquals(listOf("open", "in-progress"), active.map { it.id })
    }

    @Test
    fun `activeWork should preserve original order`() {
        val items = listOf(
            task("task1", TaskStatus.IN_PROGRESS),
            task("task2", TaskStatus.TODO),
            task("task3", TaskStatus.BLOCKED),
            task("task4", TaskStatus.DONE)
        )
        val active = items.activeWork()
        assertEquals(listOf("task1", "task2", "task3"), active.map { it.id })
    }

    @Test
    fun `activeWork should include all non-DONE statuses`() {
        val items = listOf(
            task("todo", TaskStatus.TODO),
            task("in-progress", TaskStatus.IN_PROGRESS),
            task("blocked", TaskStatus.BLOCKED)
        )
        val active = items.activeWork()
        assertEquals(3, active.size)
        assertEquals(setOf("todo", "in-progress", "blocked"), active.map { it.id }.toSet())
    }

    // TODO: groupByPriority() tests
    @Test
    fun `groupByPriority should return empty map for empty input`() {
        val items = emptyList<com.kotlinpractice.model.Task>()
        assertEquals(emptyMap(), items.groupByPriority())
    }

    @Test
    fun `groupByPriority should group tasks by priority level`() {
        val items = listOf(
            task("low").copy(priority = TaskPriority.LOW),
            task("medium").copy(priority = TaskPriority.MEDIUM),
            task("high").copy(priority = TaskPriority.HIGH),
            task("critical").copy(priority = TaskPriority.CRITICAL)
        )
        val grouped = items.groupByPriority()
        assertEquals(4, grouped.size)
        assertEquals(1, grouped[TaskPriority.LOW]?.size)
        assertEquals(1, grouped[TaskPriority.CRITICAL]?.size)
    }

    @Test
    fun `groupByPriority should preserve order within each priority group`() {
        val items = listOf(
            task("high1").copy(priority = TaskPriority.HIGH),
            task("medium1").copy(priority = TaskPriority.MEDIUM),
            task("high2").copy(priority = TaskPriority.HIGH),
            task("medium2").copy(priority = TaskPriority.MEDIUM)
        )
        val grouped = items.groupByPriority()
        assertEquals(listOf("high1", "high2"), grouped[TaskPriority.HIGH]?.map { it.id })
        assertEquals(listOf("medium1", "medium2"), grouped[TaskPriority.MEDIUM]?.map { it.id })
    }

    @Test
    fun `groupByPriority should handle mixed priorities`() {
        val items = listOf(
            task("task1").copy(priority = TaskPriority.LOW),
            task("task2").copy(priority = TaskPriority.CRITICAL),
            task("task3").copy(priority = TaskPriority.MEDIUM),
            task("task4").copy(priority = TaskPriority.CRITICAL),
            task("task5").copy(priority = TaskPriority.LOW)
        )
        val grouped = items.groupByPriority()
        assertEquals(2, grouped[TaskPriority.LOW]?.size)
        assertEquals(1, grouped[TaskPriority.MEDIUM]?.size)
        assertEquals(2, grouped[TaskPriority.CRITICAL]?.size)
    }
}
