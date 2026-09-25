package com.kotlinpractice.service

import com.kotlinpractice.model.Task
import com.kotlinpractice.model.User

/** Independent, suspending data requests for a project screen. */
interface ProjectDataSource {
    suspend fun getTasks(projectId: String): List<Task>
    suspend fun getMembers(projectId: String): List<User>
}

data class ProjectOverview(val tasks: List<Task>, val members: List<User>)

class ProjectService(private val source: ProjectDataSource) {
    /**
     * Intermediate #4: Load a project's tasks and members concurrently.
     *
     * On success, request each dependency exactly once with [projectId] and return
     * both lists unchanged in content and order, including duplicates and empty
     * lists. Neither request may need the other to finish before it starts.
     * Completion order must not affect the result. Do not mutate source-owned lists.
     * Every call performs a fresh load; calls must not share results or cancellation.
     *
     * This operation is all-or-nothing: a dependency failure propagates to the
     * caller, without retries or partial results, and cancels unfinished sibling
     * work. For a single ordinary failure, propagate the original exception.
     * If failures race, no particular exception selection is required.
     *
     * Caller cancellation and a CancellationException from either dependency must
     * terminate the load as cancellation, cancel unfinished work, and never become
     * a successful result. No dependency work may outlive this invocation: even on
     * failure or cancellation, it completes only after dependency cleanup finishes.
     * Dependencies cooperate with cancellation; cleanup may itself suspend.
     * An immediate failure need not allow the other request to start.
     *
     * Keep work within the caller's structured lifetime and coroutine context;
     * do not use detached work, blocking waits, or hard-coded dispatchers.
     * No caching, timeout policy, filtering, sorting, or deduplication is required.
     */
    suspend fun loadOverview(projectId: String): ProjectOverview = TODO("Intermediate #4")
}
