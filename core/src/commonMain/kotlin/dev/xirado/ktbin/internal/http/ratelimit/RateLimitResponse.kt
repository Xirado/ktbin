package dev.xirado.ktbin.internal.http.ratelimit

import io.ktor.http.*
import kotlinx.datetime.Clock

internal data class RateLimitResponse(
    val limit: Int,
    val remaining: Int,
    val reset: Long,
) {
    override fun toString(): String  {
        val resetIn = (reset - Clock.System.now().epochSeconds) * 1000

        return "RateLimitResponse(limit=$limit, remaining=$remaining, resetIn=$resetIn)"
    }
}

internal fun Headers.rateLimitHeaders(): RateLimitResponse? {
    val limit = get("X-Ratelimit-Limit")?.toIntOrNull()
    val remaining = get("X-Ratelimit-Remaining")?.toIntOrNull()
    val reset = get("X-Ratelimit-Reset")?.toLongOrNull()

    if (limit == null || remaining == null || reset == null)
        return null

    return RateLimitResponse(limit, remaining, reset)
}