package at.xirado.ktbin.internal.http

import at.xirado.ktbin.api.Ktbin
import io.ktor.http.*
import io.ktor.util.reflect.*

internal class Request<T, A>(
    val app: Ktbin,
    val route: Route.CompiledRoute,
    val body: T,
    val bodyType: TypeInfo,
    val resolveType: TypeInfo,
    val requestHeaders: Headers,
    val queryParameters: Parameters,
)