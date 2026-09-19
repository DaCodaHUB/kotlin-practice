package com.kotlinpractice.parser

import com.kotlinpractice.model.Task
import com.kotlinpractice.model.TaskPriority
import com.kotlinpractice.model.TaskStatus

sealed interface TaskParseResult {
    data class Success(val task: Task) : TaskParseResult
    data class Invalid(val reason: String) : TaskParseResult
}

/**
 * Beginner #6: Return a user-facing, plain-text message for this parser outcome.
 *
 * Success: "Parsed task: <id> - <title>" using the task's id and title exactly as stored.
 * Do not include the task's description, assignee, status, or priority.
 * Invalid with a nonblank reason: "Unable to parse task: <reason>".
 * Preserve a nonblank reason exactly, including surrounding whitespace.
 * Invalid with an empty or whitespace-only reason: "Unable to parse task".
 * Messages are plain text; no markup escaping is required.
 * Support outcomes constructed directly as well as those returned by a parser,
 * including success values with empty or blank id/title text. Do not revalidate
 * or modify the outcome or its task, and do not change parser behavior.
 */
fun TaskParseResult.displayMessage(): String = TODO("Beginner #6")

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
