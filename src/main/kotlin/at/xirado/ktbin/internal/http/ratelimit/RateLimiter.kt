package at.xirado.ktbin.internal.http.ratelimit

import at.xirado.ktbin.internal.http.Request
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.Instant
import kotlin.time.Duration.Companion.minutes

private val log = KotlinLogging.logger { }

internal class RateLimiter(coroutineScope: CoroutineScope) {
    private val buckets: MutableMap<String, Bucket> = mutableMapOf()
    private val mutex: Mutex = Mutex()

    init {
        coroutineScope.launch {
            while (true) {
                mutex.withLock {
                    val now = Instant.now().epochSecond
                    val toRemove = mutableListOf<String>()
                    buckets.forEach { (endpoint, bucket) ->
                        if (bucket.reset in 1..<now)
                            toRemove += endpoint
                    }

                    toRemove.forEach {
                        log.debug { "Removing expired bucket \"$it\"" }
                        buckets -= it
                    }
                }

                delay(1.minutes)
            }
        }
    }

    internal suspend fun <T> limit(request: Request<*, *>, block: suspend () -> T): Pair<T, Bucket> {
        val route = request.route
        val path = route.path

        val bucket = mutex.withLock {
            buckets.getOrPut(path) { Bucket(route.path, updateLock = mutex) }
        }

        val response = bucket.limit(block)
        return response to bucket
    }
}