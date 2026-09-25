package com.kotlinpractice.service

import com.kotlinpractice.model.Task
import com.kotlinpractice.model.TaskStatus
import com.kotlinpractice.model.User
import com.kotlinpractice.model.task
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertSame
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ProjectServiceTest {
    private val member = User("u", "Member", "member@example.com")

    private class Source(
        private val tasks: suspend (String) -> List<Task>,
        private val members: suspend (String) -> List<User>
    ) : ProjectDataSource {
        override suspend fun getTasks(projectId: String) = tasks(projectId)
        override suspend fun getMembers(projectId: String) = members(projectId)
    }

    private class SourceFailure(val marker: Any = Any()) : RuntimeException("source failed")

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `both requests overlap and results preserve data in either completion order`(
        tasksFinishFirst: Boolean
    ) = runTest {
        val tasks = listOf(task("z", TaskStatus.DONE), task("a"), task("z", TaskStatus.DONE))
        val members = listOf(member, member.copy(id = "a"), member)
        val taskGate = CompletableDeferred<Unit>()
        val memberGate = CompletableDeferred<Unit>()
        val taskCalls = mutableListOf<String>()
        val memberCalls = mutableListOf<String>()
        val service = ProjectService(Source(
            tasks = { id -> taskCalls.add(id); taskGate.await(); tasks },
            members = { id -> memberCalls.add(id); memberGate.await(); members }
        ))
        val loading = async { service.loadOverview("project-42") }
        try {
            runCurrent()
            assertEquals(listOf("project-42"), taskCalls)
            assertEquals(listOf("project-42"), memberCalls)
            assertFalse(loading.isCompleted)
            if (tasksFinishFirst) taskGate.complete(Unit) else memberGate.complete(Unit)
            runCurrent()
            assertFalse(loading.isCompleted, "Both results are required")
            taskGate.complete(Unit)
            memberGate.complete(Unit)
            runCurrent()
            assertTrue(loading.isCompleted)
            assertEquals(ProjectOverview(tasks, members), loading.await())
            assertEquals(listOf("z", "a", "z"), tasks.map { it.id })
            assertEquals(listOf("u", "a", "u"), members.map { it.id })
            assertEquals(1, taskCalls.size)
            assertEquals(1, memberCalls.size)
        } finally {
            taskGate.complete(Unit)
            memberGate.complete(Unit)
            loading.cancel()
        }
    }

    @Test
    fun `empty and mixed empty results are successful`() = runTest {
        for ((tasks, members) in listOf(
            emptyList<Task>() to emptyList<User>(),
            listOf(task("t")) to emptyList(),
            emptyList<Task>() to listOf(member)
        )) {
            var taskCalls = 0
            var memberCalls = 0
            val service = ProjectService(Source(
                { taskCalls++; tasks },
                { memberCalls++; members }
            ))
            assertEquals(ProjectOverview(tasks, members), service.loadOverview(""))
            assertEquals(1, taskCalls)
            assertEquals(1, memberCalls)
        }
    }

    @Test
    fun `each call loads fresh data for the supplied project`() = runTest {
        val taskIds = mutableListOf<String>()
        val memberIds = mutableListOf<String>()
        val service = ProjectService(Source(
            { id -> taskIds.add(id); listOf(task("$id-${taskIds.size}")) },
            { id -> memberIds.add(id); listOf(member.copy(id = "$id-${memberIds.size}")) }
        ))
        for ((index, id) in listOf("a", "a", "b").withIndex()) {
            val expectedId = "$id-${index + 1}"
            assertEquals(
                ProjectOverview(listOf(task(expectedId)), listOf(member.copy(id = expectedId))),
                service.loadOverview(id)
            )
        }
        assertEquals(listOf("a", "a", "b"), taskIds)
        assertEquals(taskIds, memberIds)
    }

    @Test
    fun `dependencies retain caller context`() = runTest {
        val names = mutableListOf<String?>()
        val service = ProjectService(Source(
            { names.add(currentCoroutineContext()[CoroutineName]?.name); emptyList() },
            { names.add(currentCoroutineContext()[CoroutineName]?.name); emptyList() }
        ))
        withContext(CoroutineName("screen-load")) {
            service.loadOverview("p")
        }
        assertEquals(listOf<String?>("screen-load", "screen-load"), names)
    }

    @ParameterizedTest
    @ValueSource(strings = ["tasks-failure", "members-failure", "tasks-cancellation", "members-cancellation"])
    fun `dependency termination cancels sibling and waits for suspending cleanup`(
        scenario: String
    ) = runTest {
        val tasksTerminate = scenario.startsWith("tasks")
        val failure = if (scenario.endsWith("cancellation")) CancellationException("source cancelled")
            else SourceFailure()
        val failGate = CompletableDeferred<Unit>()
        val cleanupGate = CompletableDeferred<Unit>()
        var taskCalls = 0
        var memberCalls = 0
        var cleanupStarted = false
        var cleanupFinished = false
        suspend fun request(terminates: Boolean) {
            if (terminates) {
                failGate.await()
                throw failure
            }
            try {
                awaitCancellation()
            } finally {
                cleanupStarted = true
                withContext(NonCancellable) { cleanupGate.await() }
                cleanupFinished = true
            }
        }
        val service = ProjectService(Source(
            { taskCalls++; request(tasksTerminate); emptyList() },
            { memberCalls++; request(!tasksTerminate); emptyList() }
        ))
        val loading = async { runCatching { service.loadOverview("p") } }
        try {
            runCurrent()
            assertEquals(1, taskCalls)
            assertEquals(1, memberCalls)
            failGate.complete(Unit)
            runCurrent()
            assertTrue(cleanupStarted, "Unfinished sibling must be cancelled")
            assertFalse(cleanupFinished)
            assertFalse(loading.isCompleted, "Cleanup is part of the invocation's lifetime")
            cleanupGate.complete(Unit)
            runCurrent()
            assertTrue(cleanupFinished)
            assertTrue(loading.isCompleted)
            val thrown = loading.await().exceptionOrNull()
            if (failure is CancellationException) assertIs<CancellationException>(thrown)
            else assertSame(failure, thrown)
            assertEquals(1, taskCalls)
            assertEquals(1, memberCalls)
        } finally {
            failGate.complete(Unit)
            cleanupGate.complete(Unit)
            loading.cancel()
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `immediate dependency failure propagates without partial result or retry`(
        tasksFail: Boolean
    ) = runTest {
        val failure = SourceFailure()
        var failingCalls = 0
        val service = ProjectService(Source(
            { if (tasksFail) { failingCalls++; throw failure }; listOf(task("t")) },
            { if (!tasksFail) { failingCalls++; throw failure }; listOf(member) }
        ))
        val result = runCatching { service.loadOverview("p") }
        assertSame(failure, result.exceptionOrNull())
        assertEquals(1, failingCalls)
    }

    @Test
    fun `caller cancellation reaches both dependencies and waits for cleanup`() = runTest {
        val cleanupGate = CompletableDeferred<Unit>()
        val started = mutableSetOf<String>()
        val cleaning = mutableSetOf<String>()
        val finished = mutableSetOf<String>()
        var returnedNormally = false
        suspend fun request(name: String): Nothing {
            started.add(name)
            try {
                awaitCancellation()
            } finally {
                cleaning.add(name)
                withContext(NonCancellable) { cleanupGate.await() }
                finished.add(name)
            }
        }
        val service = ProjectService(Source(
            { request("tasks") },
            { request("members") }
        ))
        val loading = async {
            service.loadOverview("p")
            returnedNormally = true
        }
        try {
            runCurrent()
            assertEquals(setOf("tasks", "members"), started)
            loading.cancel()
            runCurrent()
            assertEquals(started, cleaning)
            assertTrue(finished.isEmpty())
            assertFalse(loading.isCompleted)
            cleanupGate.complete(Unit)
            runCurrent()
            assertEquals(started, finished)
            assertTrue(loading.isCompleted)
            assertTrue(loading.isCancelled)
            assertFalse(returnedNormally)
        } finally {
            cleanupGate.complete(Unit)
            loading.cancel()
        }
    }

    @Test
    fun `cancelling one invocation does not cancel another on the same service`() = runTest {
        val release = CompletableDeferred<Unit>()
        val started = mutableSetOf<String>()
        val finished = mutableSetOf<String>()
        suspend fun request(id: String, kind: String) {
            started.add("$id-$kind")
            try {
                release.await()
            } finally {
                finished.add("$id-$kind")
            }
        }
        val service = ProjectService(Source(
            { id -> request(id, "tasks"); listOf(task(id)) },
            { id -> request(id, "members"); listOf(member.copy(id = id)) }
        ))
        val first = async { service.loadOverview("a") }
        val second = async { service.loadOverview("b") }
        try {
            runCurrent()
            assertEquals(setOf("a-tasks", "a-members", "b-tasks", "b-members"), started)
            first.cancel()
            runCurrent()
            assertTrue(first.isCompleted)
            assertEquals(setOf("a-tasks", "a-members"), finished)
            assertFalse(second.isCompleted)
            release.complete(Unit)
            runCurrent()
            assertEquals(
                ProjectOverview(listOf(task("b")), listOf(member.copy(id = "b"))),
                second.await()
            )
            assertEquals(started, finished)
        } finally {
            release.complete(Unit)
            first.cancel()
            second.cancel()
        }
    }
}
