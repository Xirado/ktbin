package dev.xirado.ktbin.internal.http

import dev.xirado.ktbin.api.GobinHost
import io.ktor.http.*

internal fun GobinHost.createUrl(
    route: Route.CompiledRoute,
    parameters: Parameters = Parameters.Empty
): Url = URLBuilder(protocol, host, port).apply {
    appendPathSegments(route.path, encodeSlash = false)
    this.parameters.appendAll(parameters)
}.build()

internal fun GobinHost.createUrl(vararg pathSegments: String): Url = URLBuilder(protocol, host, port).apply {
    appendPathSegments(pathSegments.toList(), encodeSlash = false)
}.build()

internal fun GobinHost.createUrl(path: String): Url = URLBuilder(protocol, host, port).apply {
    appendPathSegments(path, encodeSlash = false)
}.build()