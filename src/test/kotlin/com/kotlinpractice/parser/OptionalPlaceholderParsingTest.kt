package com.kotlinpractice.parser

import com.kotlinpractice.model.Task
import com.kotlinpractice.model.TaskPriority
import com.kotlinpractice.model.TaskStatus
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class OptionalPlaceholderParsingTest {
    private val parser = DelimitedTaskParser()
    private val expected = Task(
        id = "T-1",
        title = "Ship  report",
        description = null,
        status = TaskStatus.IN_PROGRESS,
        priority = TaskPriority.HIGH,
        assigneeId = null
    )

    @Test
    fun `parses complete record with surrounding whitespace and mixed case enums`() {
        assertEquals(
            TaskParseResult.Success(expected.copy(assigneeId = "u-7", description = "Final  review")),
            parser.parse(
                "\t T-1 | Ship  report \t| iN_pRoGrEsS | hIgH | u-7 | Final  review \n"
            )
        )
    }

    @Test
    fun `accepts omitted optional fields and assignee without description`() {
        assertEquals(
            TaskParseResult.Success(expected),
            parser.parse("T-1|Ship  report|IN_PROGRESS|HIGH")
        )
        assertEquals(
            TaskParseResult.Success(expected.copy(assigneeId = "u-7")),
            parser.parse("T-1|Ship  report|IN_PROGRESS|HIGH|u-7")
        )
    }

    @Test
    fun `accepts empty blank and placeholder optional fields`() {
        for (optional in listOf("", " \t ", "-", " \t- ")) {
            assertEquals(
                TaskParseResult.Success(expected),
                parser.parse("T-1|Ship  report|IN_PROGRESS|HIGH|$optional")
            )
            for (description in listOf("", " \t ", "-", " - ")) {
                assertEquals(
                    TaskParseResult.Success(expected),
                    parser.parse("T-1|Ship  report|IN_PROGRESS|HIGH|$optional|$description")
                )
            }
        }
    }

    @Test
    fun `optional fields are independent`() {
        assertEquals(
            TaskParseResult.Success(expected.copy(description = "Review")),
            parser.parse("T-1|Ship  report|IN_PROGRESS|HIGH|-|Review")
        )
        assertEquals(
            TaskParseResult.Success(expected.copy(assigneeId = "u-7")),
            parser.parse("T-1|Ship  report|IN_PROGRESS|HIGH|u-7|-")
        )
    }

    @Test
    fun `preserves literal hyphens outside optional placeholders`() {
        assertEquals(
            TaskParseResult.Success(expected.copy(id = "-", title = "-", assigneeId = "--", description = "Follow-up")),
            parser.parse("-| - |IN_PROGRESS|HIGH|--|Follow-up")
        )
    }

    @Test
    fun `rejects malformed field counts with useful reason`() {
        for (input in listOf("", "   ", "T-1", "T-1|Title", "T-1|Title|TODO", "T-1|Title|TODO|LOW|||extra", "T-1|Title|TODO|LOW|||")) {
            assertEquals(TaskParseResult.Invalid("Expected 4 to 6 fields"), parser.parse(input))
        }
    }

    @Test
    fun `validates field count before required text and enums`() {
        for (input in listOf("| |BAD", "| |BAD|BAD|||")) {
            assertEquals(TaskParseResult.Invalid("Expected 4 to 6 fields"), parser.parse(input))
        }
    }

    @Test
    fun `rejects blank required text before validating enums`() {
        for (input in listOf("|Title|TODO|LOW", " \t |Title|TODO|LOW", "T-1||TODO|LOW", "T-1| \t |BAD|BAD")) {
            assertEquals(TaskParseResult.Invalid("Task id and title are required"), parser.parse(input))
        }
    }

    @Test
    fun `rejects unknown blank and placeholder enums with specific reasons`() {
        for (status in listOf("LATER", "", " \t ", "-")) {
            assertEquals(
                TaskParseResult.Invalid("Unknown task status"),
                parser.parse("T-1|Title|$status|BAD")
            )
        }
        for (priority in listOf("URGENT", "", " \t ", "-")) {
            assertEquals(
                TaskParseResult.Invalid("Unknown task priority"),
                parser.parse("T-1|Title|TODO|$priority")
            )
        }
    }
}
