package com.kotlinpractice.model

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TaskModelsTest {
    @Test
    fun `tasks use value equality`() {
        assertEquals(task("T-1"), task("T-1"))
    }
}
