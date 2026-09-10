package com.kotlinpractice.parser

import com.kotlinpractice.model.TaskPriority
import com.kotlinpractice.model.TaskStatus
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class DelimitedTaskParserTest {
    private val parser = DelimitedTaskParser()

    @Test
    fun `parses a complete task record`() {
        val result = assertIs<TaskParseResult.Success>(
            parser.parse("T-1 | Ship report | in_progress | high | u-7 | Final review")
        )

        assertEquals("T-1", result.task.id)
        assertEquals(TaskStatus.IN_PROGRESS, result.task.status)
        assertEquals(TaskPriority.HIGH, result.task.priority)
        assertEquals("Final review", result.task.description)
    }

    @Test
    fun `rejects missing required fields and unknown enums`() {
        assertIs<TaskParseResult.Invalid>(parser.parse(" | Title | TODO | LOW"))
        assertIs<TaskParseResult.Invalid>(parser.parse("T-1 | Title | LATER | LOW"))
    }
}
