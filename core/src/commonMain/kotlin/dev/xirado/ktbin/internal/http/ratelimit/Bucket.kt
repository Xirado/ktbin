package dev.xirado.ktbin.internal.http.ratelimit

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock

private val log = KotlinLogging.logger { }

internal class Bucket(
    val path: String,
    limit: Int = 0,
    remaining: Int = 0,
    reset: Long = 0,
    val updateLock: Mutex,
) {
    var limit: Int = limit
        private set
    var remaining: Int = remaining
        private set
    var reset: Long = reset
        private set

    private val execLock = Mutex()

    suspend fun <T> limit(block: suspend () -> T): T {
        val delay: Long = updateLock.withLock {
            if (reset == 0L)
                return@withLock 0L

            val currentTime = Clock.System.now().epochSeconds

            if (remaining <= 0 && reset > currentTime)
                (reset - currentTime) * 1000
            else
                0L
        }

        return execLock.withLock {
            if (delay > 0) {
                log.debug { "Waiting ${delay}ms to avoid rate-limit on $path" }
                delay(delay)
            }
            block()
        }
    }

    suspend fun update(limit: Int? = null, remaining: Int? = null, reset: Long? = null) {
        updateLock.withLock {
            limit?.let { this.limit = it }
            remaining?.let { this.remaining = it }
            reset?.let { this.reset = it }
        }
    }

    override fun toString(): String = "Bucket(path=$path, limit=$limit, remaining=$remaining, reset=$reset)"
}