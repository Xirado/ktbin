package dev.xirado.ktbin.internal.http

import dev.xirado.ktbin.api.GobinHost
import dev.xirado.ktbin.api.Ktbin
import dev.xirado.ktbin.api.exception.GobinAPIException
import dev.xirado.ktbin.api.json
import dev.xirado.ktbin.internal.http.ratelimit.RateLimiter
import dev.xirado.ktbin.internal.http.ratelimit.rateLimitHeaders
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.serializer

private val log = KotlinLogging.logger { }

internal class Requester(private val app: Ktbin, private val host: GobinHost) {
    private val rateLimiter = RateLimiter(app.scope)

    @OptIn(InternalSerializationApi::class)
    @Suppress("UNCHECKED_CAST")
    suspend fun <T, A> request(request: Request<T, A>): A {
        val route = request.route

        val url = host.createUrl(route, request.queryParameters)

        log.debug { "Preparing request $route" }
        val (response, bucket) = rateLimiter.limit(request) {
            log.debug { "Executing request $route" }
            app.httpClient.request(url) {
                method = route.route.httpMethod
                userAgent("Ktbin (https://github.com/xirado/ktbin)")

                val body = request.body

                if (request.bodyType.type != Unit::class && body != null)
                    setBody(body, request.bodyType)

                headers.appendAll(request.requestHeaders)
            }
        }

        val headers = response.headers
        val rateLimitHeaders = headers.rateLimitHeaders()

        rateLimitHeaders?.let {
            log.debug { "Updating bucket ${bucket.path}" }
            bucket.update(
                limit = it.limit,
                remaining = it.remaining,
                reset = it.reset,
            )
        }

        val responseStatus = response.status
        val jsonBody = response.bodyAsText()

        val resolveType = request.resolveType
        val bodyExpected = resolveType.type != Unit::class
        val isResponseTypeNullable = resolveType.kotlinType?.isMarkedNullable ?: false

        if (bodyExpected && responseStatus.value == 204) {
            if (isResponseTypeNullable)
                return null as A

            val resolveTypeName = resolveType.type.simpleName
            throw IllegalStateException("Expected response body of type $resolveTypeName but got 204 No content")
        }

        if (!responseStatus.isSuccess()) {
            val error = try {
                json.decodeFromString<GobinAPIException>(jsonBody)
            } catch (e: IllegalArgumentException) {
                GobinAPIException(jsonBody, response.status.value, "N/A", "N/A")
            } catch (e: SerializationException) {
                e
            }

            if (error is GobinAPIException) {
                when (error.status) {
                    429 -> {
                        log.warn { "Encountered 429 on route $route" }
                        return request(request)
                    }

                    404 -> {
                        if (!isResponseTypeNullable)
                            throw error

                        return null as A
                    }
                }
            }

            throw error
        }

        if (!bodyExpected)
            return Unit as A

        return json.decodeFromString(resolveType.type.serializer(), jsonBody) as A
    }
}