package com.kotlinpractice.cache

/**
 * Intermediate #3: A generic cache with least-recently-used eviction.
 *
 * [capacity] is the maximum number of stored keys and must be positive.
 * Construction with zero or negative capacity throws IllegalArgumentException.
 * A new cache is empty. Keys use equality semantics; values are non-null.
 * Nullable key types are supported, with null behaving like any other key.
 *
 * A successful get returns the stored value and makes its key most recently used.
 * A missing get returns null and leaves stored keys' relative recency unchanged.
 * Every put makes its key most recently used. Putting an existing key replaces its
 * value without evicting another key, even when the value is unchanged. Putting a
 * new key into a full cache evicts the least recently used key.
 *
 * Remove immediately invalidates a key without changing other keys' relative
 * recency. Removing an absent key has no effect. Removed and evicted keys may be
 * inserted again. Each instance has independent contents and recency.
 *
 * Values are returned as stored, without copying. There is no expiration, traversal
 * order API, or concurrency guarantee. Preserve the existing Cache API and the
 * completed InMemoryCache and InMemoryTaskCache behavior.
 */
class LruCache<K, V : Any>(
    private val capacity: Int
) : Cache<K, V> {

    private data class Node<K, V>(
        val key: K,
        var value: V,
        var left: Node<K, V>?,
        var right: Node<K, V>?
    )

    private val cacheMap = mutableMapOf<K, Node<K, V>>()
    private var head: Node<K, V>? = null
    private var tail: Node<K, V>? = null

    init {
        require(capacity > 0)
    }

    override fun get(key: K): V? {
        val node = cacheMap[key] ?: return null
        removeNode(node)
        addToFront(node)
        return node.value
    }

    override fun put(key: K, value: V) {
        val node = cacheMap[key]
        if (node != null) {
            node.value = value
            removeNode(node)
            addToFront(node)
        } else {
            val node  = Node(key, value, null, null)
            addToFront(node)
            cacheMap[key] = node
            if (cacheMap.size > capacity) {
                val node = tail
                cacheMap.remove(node?.key)
                removeNode(node!!)
            }
        }
    }

    override fun remove(key: K) {
        val node = cacheMap[key] ?: return
        cacheMap.remove(key)
        removeNode(node)
    }

    private fun addToFront(node: Node<K, V>) {
        if (head == null && tail == null) {
            head = node
            tail = node
        } else {
            node.right = head
            head?.left = node
            head = node
        }
    }

    private fun removeNode(node: Node<K, V>) {
        if (node == head) {
            head = node.right
            node.right?.left = null
        }
        if (node == tail) {
            tail = node.left
            node.left?.right = null
        }
        node.left?.right = node.right
        node.right?.left = node.left
        node.left = null
        node.right = null
    }
}
