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
    /**
     * Beginner #5: Parse a record with optional-field placeholders.
     *
     * Format: id | title | status | priority | assigneeId | description.
     * Accept exactly 4, 5, or 6 fields; the last two fields are optional, in that order.
     * Ignore surrounding whitespace in each field, preserving whitespace inside its text.
     * Missing, empty, or whitespace-only optional fields become null.
     * Input variation: an optional field containing exactly "-" after surrounding
     * whitespace is removed also becomes null. A hyphen elsewhere remains literal text;
     * this placeholder rule does not apply to required fields.
     * Status and priority accept existing enum names without regard to letter case.
     *
     * Return Success with the resulting Task for valid input. For malformed input,
     * return Invalid rather than throwing, with these reasons in validation order:
     * - Outside 4..6 fields: "Expected 4 to 6 fields".
     * - Empty or whitespace-only id or title: "Task id and title are required".
     * - Unrecognized or blank status: "Unknown task status".
     * - Unrecognized or blank priority: "Unknown task priority".
     * Every pipe is a field separator; quoted or escaped separators are not supported.
     */
    override fun parse(input: String): TaskParseResult {
        val fields = input
            .split("|")
            .map { it.trim() }

        if (fields.size !in 4..6) {
            return TaskParseResult.Invalid("Expected 4 to 6 fields")
        }

        val id = fields[0]
        val title = fields[1]
        if (id.isBlank() || title.isBlank()) {
            return TaskParseResult.Invalid("Task id and title are required")
        }

        val status = TaskStatus.entries.firstOrNull {
            it.name.equals(fields[2], ignoreCase = true)
        } ?: return TaskParseResult.Invalid("Unknown task status")

        val priority = TaskPriority.entries.firstOrNull {
            it.name.equals(fields[3], ignoreCase = true)
        } ?: return TaskParseResult.Invalid("Unknown task priority")

        val assigneeId = fields.getOrNull(4)?.let {
            if (it.isBlank() || it == "-") {
                null
            } else {
                it
            }
        }
        val description = fields.getOrNull(5)?.let {
            if (it.isBlank() || it == "-") {
                null
            } else {
                it
            }
        }

        val task = Task (
            id = id,
            title = title,
            status = status,
            priority = priority,
            assigneeId = assigneeId,
            description = description
        )

        return TaskParseResult.Success(task)
    }
}
