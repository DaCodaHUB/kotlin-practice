package com.kotlinpractice.parser

import com.kotlinpractice.model.TaskPriority
import com.kotlinpractice.model.TaskStatus
import com.kotlinpractice.model.task
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TaskParseResultDisplayMessageTest {
    @Test
    fun `success displays task id and title`() {
        val result = TaskParseResult.Success(task("T-1").copy(title = "Review changes"))

        assertEquals("Parsed task: T-1 - Review changes", result.displayMessage())
    }

    @Test
    fun `success excludes other task fields for every status and priority`() {
        for (status in TaskStatus.entries) {
            for (priority in TaskPriority.entries) {
                val result = TaskParseResult.Success(
                    task("T-2", status).copy(
                        title = "Review",
                        priority = priority,
                        description = "Private description",
                        assigneeId = "user-7"
                    )
                )
                val original = result.copy(task = result.task.copy())

                assertEquals("Parsed task: T-2 - Review", result.displayMessage())
                assertEquals(original, result)
            }
        }
    }

    @Test
    fun `success preserves whitespace unicode and literal punctuation`() {
        val result = TaskParseResult.Success(
            task("  T-3  ").copy(title = "  Review <UI> & café\nchanges  ")
        )

        assertEquals("Parsed task:   T-3   -   Review <UI> & café\nchanges  ", result.displayMessage())
    }

    @Test
    fun `success does not revalidate empty or blank id and title`() {
        assertEquals(
            "Parsed task:  - ",
            TaskParseResult.Success(task("").copy(title = "")).displayMessage()
        )
        assertEquals(
            "Parsed task:   - \t",
            TaskParseResult.Success(task(" ").copy(title = "\t")).displayMessage()
        )
    }

    @Test
    fun `invalid displays a nonblank reason exactly`() {
        for (reason in listOf("Unknown task status", "  Bad <input> & café\ntry again  ")) {
            val result = TaskParseResult.Invalid(reason)

            assertEquals("Unable to parse task: $reason", result.displayMessage())
            assertEquals(TaskParseResult.Invalid(reason), result)
        }
    }

    @Test
    fun `invalid with empty or whitespace only reason displays fallback`() {
        for (reason in listOf("", " ", "\t", "\r\n", " \t\r\n ")) {
            assertEquals("Unable to parse task", TaskParseResult.Invalid(reason).displayMessage())
        }
    }

    @Test
    fun `messages support actual parser outcomes without changing parsing`() {
        val parser = DelimitedTaskParser()
        val success = parser.parse("T-4 | Review | TODO | HIGH | - | -")
        val invalid = parser.parse("T-4 | Review | UNKNOWN | HIGH")

        assertEquals(
            TaskParseResult.Success(task("T-4").copy(title = "Review", priority = TaskPriority.HIGH)),
            success
        )
        assertEquals(TaskParseResult.Invalid("Unknown task status"), invalid)
        assertEquals("Parsed task: T-4 - Review", success.displayMessage())
        assertEquals("Unable to parse task: Unknown task status", invalid.displayMessage())
    }
}
