package com.kotlinpractice.repository

import com.kotlinpractice.model.Task
import com.kotlinpractice.model.task
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class RepositoryErrorHandlingTest {
    @Test
    fun `empty provider list returns an empty result`() = runTest {
        assertEquals(TaskLoadResult(emptyList(), emptyList()), loadTasksWithFailures(emptyList()))
    }

    @Test
    fun `successful provider returns its complete unchanged snapshot`() = runTest {
        val tasks = listOf(task("one"), task("two"))
        val provider = Provider { tasks }

        assertEquals(TaskLoadResult(tasks, emptyList()), loadTasksWithFailures(listOf(provider)))
        assertEquals(1, provider.calls)
    }

    @Test
    fun `successful providers preserve order duplicates and input lists`() = runTest {
        val first = task("shared")
        val second = first.copy(title = "Another version")
        val firstTasks = mutableListOf(first, first)
        val secondTasks = mutableListOf(second, task("last"))
        val providers = mutableListOf(Provider { firstTasks }, Provider { secondTasks })
        val originalProviders = providers.toList()

        val result = loadTasksWithFailures(providers)

        assertEquals(listOf(first, first, second, task("last")), result.tasks)
        assertTrue(result.failures.isEmpty())
        assertEquals(listOf(first, first), firstTasks)
        assertEquals(listOf(second, task("last")), secondTasks)
        assertEquals(originalProviders, providers)
        assertEquals(listOf(1, 1), providers.map { it.calls })
    }

    @Test
    fun `empty snapshots are successful`() = runTest {
        val providers = listOf(Provider { emptyList() }, Provider { emptyList() })

        assertEquals(TaskLoadResult(emptyList(), emptyList()), loadTasksWithFailures(providers))
        assertEquals(listOf(1, 1), providers.map { it.calls })
    }

    @Test
    fun `provider exception is reported with original cause and index`() = runTest {
        val failure = IOException("unavailable")
        val provider = Provider { throw failure }

        val result = loadTasksWithFailures(listOf(provider))

        assertTrue(result.tasks.isEmpty())
        assertEquals(1, result.failures.size)
        assertEquals(0, result.failures.single().providerIndex)
        assertSame(failure, result.failures.single().cause)
        assertEquals(1, provider.calls)
    }

    @Test
    fun `mixed providers retain successes around multiple failures`() = runTest {
        val firstFailure = IOException("offline")
        val secondFailure = IllegalStateException("invalid state")
        val providers = listOf(
            Provider { throw firstFailure },
            Provider { listOf(task("one")) },
            Provider { throw secondFailure },
            Provider { emptyList() },
            Provider { listOf(task("two"), task("three")) }
        )

        val result = loadTasksWithFailures(providers)

        assertEquals(listOf(task("one"), task("two"), task("three")), result.tasks)
        assertEquals(listOf(0, 2), result.failures.map { it.providerIndex })
        assertSame(firstFailure, result.failures[0].cause)
        assertSame(secondFailure, result.failures[1].cause)
        assertEquals(List(5) { 1 }, providers.map { it.calls })
    }

    @Test
    fun `all failures remain individually identifiable`() = runTest {
        val failures = listOf(IOException(), IllegalArgumentException(), Exception("other"))
        val providers = failures.map { failure -> Provider { throw failure } }

        val result = loadTasksWithFailures(providers)

        assertTrue(result.tasks.isEmpty())
        assertEquals(listOf(0, 1, 2), result.failures.map { it.providerIndex })
        failures.forEachIndexed { index, failure -> assertSame(failure, result.failures[index].cause) }
        assertEquals(List(3) { 1 }, providers.map { it.calls })
    }

    @Test
    fun `repeated provider occurrences are each loaded`() = runTest {
        val provider = Provider { listOf(task("same")) }

        val result = loadTasksWithFailures(listOf(provider, provider))

        assertEquals(listOf(task("same"), task("same")), result.tasks)
        assertTrue(result.failures.isEmpty())
        assertEquals(2, provider.calls)
    }

    @Test
    fun `subsequent loads reflect provider recovery and fresh data`() = runTest {
        val failure = IOException("temporary")
        var unavailable = true
        val provider = Provider {
            if (unavailable) throw failure
            listOf(task("recovered"))
        }

        val first = loadTasksWithFailures(listOf(provider))
        assertEquals(TaskLoadResult(emptyList(), listOf(TaskProviderFailure(0, failure))), first)
        unavailable = false
        assertEquals(
            TaskLoadResult(listOf(task("recovered")), emptyList()),
            loadTasksWithFailures(listOf(provider))
        )
        assertEquals(2, provider.calls)
    }

    @Test
    fun `provider cancellation propagates as the original instance`() = runTest {
        val cancellation = CancellationException("provider cancelled")

        val actual = assertFailsWith<CancellationException> {
            loadTasksWithFailures(listOf(Provider { throw cancellation }))
        }

        assertSame(cancellation, actual)
    }

    @Test
    fun `cancellation is not hidden by preceding successes or failures`() = runTest {
        val cancellation = CancellationException("cancelled")
        val providers = listOf(
            Provider { listOf(task("usable")) },
            Provider { throw IOException("failed") },
            Provider { throw cancellation }
        )

        assertSame(cancellation, assertFailsWith<CancellationException> {
            loadTasksWithFailures(providers)
        })
    }

    @Test
    fun `caller cancellation during suspension does not return a result`() = runTest {
        val entered = CompletableDeferred<Unit>()
        var returned = false
        val provider = Provider {
            entered.complete(Unit)
            awaitCancellation()
        }
        val loading = launch {
            loadTasksWithFailures(listOf(provider))
            returned = true
        }

        entered.await()
        loading.cancelAndJoin()

        assertTrue(loading.isCancelled)
        assertFalse(returned)
    }

    @Test
    fun `JVM errors propagate unchanged`() = runTest {
        val failure = AssertionError("fatal")

        assertSame(failure, assertFailsWith<AssertionError> {
            loadTasksWithFailures(listOf(Provider { throw failure }))
        })
    }

    private class Provider(private val snapshot: suspend () -> List<Task>) : TaskRepository {
        var calls = 0
            private set

        override suspend fun getTasks(): List<Task> {
            calls++
            return snapshot()
        }

        override suspend fun getTask(id: String): Task? = error("Unexpected getTask call")

        override fun observeTasks(): Flow<List<Task>> = error("Unexpected observeTasks call")
    }
}
