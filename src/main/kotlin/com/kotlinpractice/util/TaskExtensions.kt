package com.kotlinpractice.util

import com.kotlinpractice.model.Task
import com.kotlinpractice.model.TaskStatus

fun Iterable<Task>.groupByAssignee(): Map<String?, List<Task>> = groupBy(Task::assigneeId)

fun Iterable<Task>.completed(): List<Task> = filter { it.status == TaskStatus.DONE }
