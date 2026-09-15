package com.kotlinpractice.util

import com.kotlinpractice.model.Task
import com.kotlinpractice.model.TaskPriority
import com.kotlinpractice.model.TaskStatus

/**
 * Beginner #3: Return a display-ready label without modifying the task.
 *
 * Required format: "[PRIORITY] title (STATUS)".
 * Use the exact priority and status names, including underscores and uppercase letters.
 * When description is non-null and not blank, append " - description".
 * For null, empty, or whitespace-only descriptions, omit that suffix entirely.
 * Preserve title and nonblank description text exactly, including surrounding whitespace.
 * Do not include the task ID or assignee. Support every priority and status.
 * Example: "[HIGH] Review pull request (IN_PROGRESS) - Check the tests".
 */
fun Task.displayLabel(): String =
    "[${priority}] $title (${status})" +
            if (description != null) {
                " - $description"
            } else {
                ""
            }

fun Iterable<Task>.groupByAssignee(): Map<String?, List<Task>> = groupBy(Task::assigneeId)

fun Iterable<Task>.completed(): List<Task> = filter { it.status == TaskStatus.DONE }

/**
 * Filters tasks to return only active work (non-completed tasks).
 * Preserves the original collection order.
 */
fun Iterable<Task>.activeWork(): List<Task> = filter { it.status == TaskStatus.IN_PROGRESS }

/**
 * Groups tasks by priority level.
 * Preserves the original order of tasks within each priority group.
 */
fun Iterable<Task>.groupByPriority(): Map<TaskPriority, List<Task>> = groupBy(Task::priority)
