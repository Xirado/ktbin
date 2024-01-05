package at.xirado.ktbin.internal.http

import io.ktor.http.*

private val routeParamRegex = """\{(\w+)}""".toRegex()

internal class Route private constructor(val httpMethod: HttpMethod, val path: String) {
    fun compile(vararg args: String): CompiledRoute {
        var currentParamCount = 0

        val compiledPath = routeParamRegex.replace(path) {
            currentParamCount++

            if (args.size < currentParamCount)
                throw IllegalArgumentException("Invalid amount of passed route parameters")

            args[currentParamCount - 1]
        }

        return CompiledRoute(this, compiledPath)
    }

    class CompiledRoute(val route: Route, val path: String) {
        override fun toString(): String = "Route(${route.httpMethod.value} $path)"
    }

    companion object {
        val GET_DOCUMENT = Route(HttpMethod.Get, "/documents/{key}")
        val GET_DOCUMENT_VERSIONS = Route(HttpMethod.Get, "/documents/{key}/versions")
        val GET_DOCUMENT_VERSION = Route(HttpMethod.Get, "/documents/{key}/versions/{version}")
        val GET_DOCUMENT_FILE = Route(HttpMethod.Get, "/documents/{key}/files/{fileName}")
        val GET_DOCUMENT_VERSION_FILE = Route(HttpMethod.Get, "/documents/{key}/versions/{version}/files/{fileName}")
        val GET_DOCUMENT_PREVIEW = Route(HttpMethod.Get, "/{key}/preview")
        val GET_DOCUMENT_VERSION_PREVIEW = Route(HttpMethod.Get, "/{key}/{version}/preview")

        val CREATE_DOCUMENT = Route(HttpMethod.Post, "/documents")

        val UPDATE_DOCUMENT = Route(HttpMethod.Patch, "/documents/{key}")
        val SHARE_DOCUMENT = Route(HttpMethod.Post, "/documents/{key}/share")
        val DELETE_DOCUMENT = Route(HttpMethod.Delete, "/documents/{key}/delete")
        val DELETE_DOCUMENT_VERSION = Route(HttpMethod.Delete, "/documents/{key}/versions/{version}")
    }
}