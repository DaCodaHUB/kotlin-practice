package com.kotlinpractice.parser

import com.kotlinpractice.model.Task
import com.kotlinpractice.model.TaskPriority
import com.kotlinpractice.model.TaskStatus

sealed interface TaskParseResult {
    data class Success(val task: Task) : TaskParseResult
    data class Invalid(val reason: String) : TaskParseResult
}

interface TaskParser {
    fun parse(input: String): TaskParseResult
}

/** Parses pipe-delimited records: id | title | status | priority | assigneeId | description. */
class DelimitedTaskParser : TaskParser {
    override fun parse(input: String): TaskParseResult {
        val fields = input.split('|').map(String::trim)
        if (fields.size !in 4..6) return TaskParseResult.Invalid("Expected 4 to 6 fields")

        val id = fields[0]
        val title = fields[1]
        if (id.isBlank() || title.isBlank()) return TaskParseResult.Invalid("Task id and title are required")

        val status = enumValueOrNull<TaskStatus>(fields[2])
            ?: return TaskParseResult.Invalid("Unknown task status")
        val priority = enumValueOrNull<TaskPriority>(fields[3])
            ?: return TaskParseResult.Invalid("Unknown task priority")

        return TaskParseResult.Success(
            Task(
                id = id,
                title = title,
                description = fields.getOrNull(5)?.takeIf(String::isNotBlank),
                status = status,
                priority = priority,
                assigneeId = fields.getOrNull(4)?.takeIf(String::isNotBlank)
            )
        )
    }

    private inline fun <reified T : Enum<T>> enumValueOrNull(value: String): T? =
        enumValues<T>().firstOrNull { it.name.equals(value, ignoreCase = true) }
}
