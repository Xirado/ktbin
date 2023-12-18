package at.xirado.ktbin.http.ratelimit

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.Instant

private val log = KotlinLogging.logger { }

class Bucket(val path: String, limit: Int = 0, remaining: Int = 0, reset: Long = 0) {
    var limit: Int = limit
        private set
    var remaining: Int = remaining
        private set
    var reset: Long = reset
        private set

    private val mutex = Mutex()
    private val executionMutex = Mutex()

    suspend fun <T> limit(block: suspend () -> T): T {
        val delay: Long = mutex.withLock {
            if (reset == 0L)
                return@withLock 0L

            if (remaining <= 0 && reset > Instant.now().epochSecond)
                (reset - Instant.now().epochSecond) * 1000
            else
                0L
        }

        return executionMutex.withLock {
            if (delay > 0) {
                log.debug { "Waiting ${delay}ms to avoid rate-limit on $path" }
                delay(delay)
            }
            block()
        }
    }

    suspend fun update(limit: Int? = null, remaining: Int? = null, reset: Long? = null) {
        mutex.withLock {
            limit?.let { this.limit = it }
            remaining?.let { this.remaining = it }
            reset?.let { this.reset = it }
        }
    }

    override fun toString(): String = "Bucket(path=$path, limit=$limit, remaining=$remaining, reset=$reset)"
}