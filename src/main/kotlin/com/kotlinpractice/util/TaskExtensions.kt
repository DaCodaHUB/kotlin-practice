package com.kotlinpractice.util

import com.kotlinpractice.model.Task
import com.kotlinpractice.model.TaskPriority
import com.kotlinpractice.model.TaskStatus

fun Iterable<Task>.groupByAssignee(): Map<String?, List<Task>> = groupBy(Task::assigneeId)

fun Iterable<Task>.completed(): List<Task> = filter { it.status == TaskStatus.DONE }

/**
 * Filters tasks to return only active work (non-completed tasks).
 * Preserves the original collection order.
 * TODO: Implement filtering logic
 */
fun Iterable<Task>.activeWork(): List<Task> = filter { it.status == TaskStatus.IN_PROGRESS }

/**
 * Groups tasks by priority level.
 * Preserves the original order of tasks within each priority group.
 * TODO: Implement grouping logic
 */
fun Iterable<Task>.groupByPriority(): Map<TaskPriority, List<Task>> = groupBy(Task::priority)
