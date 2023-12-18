package at.xirado.ktbin.http

import io.ktor.http.*

data class GobinHost(val host: String, val protocol: URLProtocol = URLProtocol.HTTPS, val port: Int = 443) {
    val cre: String
}

val defaultGobinHost = GobinHost("xgob.in")

internal fun GobinHost.createUrl(route: Route.CompiledRoute, parameters: Parameters = Parameters.Empty): Url = URLBuilder(protocol, host, port).apply {
    appendPathSegments(route.path, encodeSlash = false)
    this.parameters.appendAll(parameters)
}.build()

internal fun GobinHost.createUrl(vararg pathSegments: String): Url = URLBuilder(protocol, host, port).apply {
    appendPathSegments(pathSegments.toList(), encodeSlash = false)
}.build()

internal fun GobinHost.createUrl(path: String): Url = URLBuilder(protocol, host, port).apply {
    appendPathSegments(path, encodeSlash = false)
}.build()