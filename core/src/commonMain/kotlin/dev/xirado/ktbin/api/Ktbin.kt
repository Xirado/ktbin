package dev.xirado.ktbin.api

import dev.xirado.ktbin.api.entity.Document
import dev.xirado.ktbin.api.entity.DocumentFile
import dev.xirado.ktbin.internal.Permissions
import dev.xirado.ktbin.internal.buildMultipartBody
import dev.xirado.ktbin.internal.createCoroutineScope
import dev.xirado.ktbin.internal.entity.*
import dev.xirado.ktbin.internal.http.Request
import dev.xirado.ktbin.internal.http.Requester
import dev.xirado.ktbin.internal.http.Route
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.reflect.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json

private val log = KotlinLogging.logger { }

/**
 * The core of Ktbin.
 *
 * The entire Gobin HTTP API can be accessed from this class.
 */
class Ktbin internal constructor(
    val host: GobinHost,
    internal val coroutineDispatcher: CoroutineDispatcher,
    internal val httpClient: HttpClient,
) {
    internal val parentJob = SupervisorJob()
    internal val scope = createCoroutineScope(parentJob, coroutineDispatcher, log)
    internal val requester = Requester(this, host)

    /**
     * Retrieves a [Document] using the provided [key].
     *
     * @param key       The unique identifier of the document.
     * @param version   Returns a [snapshot][Document.version] of this document, or `null` to get the latest version.
     * @param formatter [Formatter] used to [format][DocumentFile.formatted] files for certain environments.
     * @param styleName Style to use for formatting, only works in combination with [formatter].
     *
     * @throws IllegalArgumentException If [styleName] is specified without [formatter].
     *
     * @return [Document] associated with [key], or `null` if such document, or snapshot, does not exist.
     */
    suspend fun getDocument(
        key: String,
        version: Long? = null,
        formatter: Formatter? = null,
        styleName: String? = null,
    ): Document? {
        require(!(styleName != null && formatter == null)) { "Setting style name only works when specifying a formatter!" }

        val parameters = parameters {
            formatter?.let { this["formatter"] = it.id }
            styleName?.let { this["style"] = it }
        }

        val route = when {
            version != null -> Route.GET_DOCUMENT_VERSION.compile(key, version.toString())
            else -> Route.GET_DOCUMENT.compile(key)
        }

        val response: ApiDocument? = request(route, parameters = parameters)

        return response?.let { DocumentImpl(it, this) }
    }

    /**
     * Retrieves a single [DocumentFile] from a document.
     *
     * @param key       The unique identifier of the document.
     * @param fileName  The name of the file to retrieve.
     * @param version   Returns a [snapshot][Document.version] of this file, or `null` to get the latest version.
     * @param formatter [Formatter] used to [format][DocumentFile.formatted] the file for certain environments.
     * @param styleName Style to use for formatting, only works in combination with [formatter].
     * @param language Overrides the [Language] used for formatting and syntax highlighting.
     *
     * @throws IllegalArgumentException If [styleName] is specified without [formatter].
     *
     * @return [DocumentFile] of document [key] named [fileName], or `null` if it doesn't exist. (Bad key, fileName or version)
     */
    suspend fun getDocumentFile(
        key: String,
        fileName: String,
        version: Long? = null,
        formatter: Formatter? = null,
        styleName: String? = null,
        language: Language? = null
    ): DocumentFile? {
        require(!(styleName != null && formatter == null)) { "Setting style name only works when specifying a formatter!" }

        val parameters = parameters {
            formatter?.let { this["formatter"] = it.id }
            styleName?.let { this["style"] = it }
            fileName.let { this["file"] = it }
            language?.let { this["language"] = it.id }
        }

        val route = when {
            version != null -> Route.GET_DOCUMENT_VERSION_FILE.compile(key, version.toString(), fileName)
            else -> Route.GET_DOCUMENT_FILE.compile(key, fileName)
        }

        val response: ApiFile? = request(route, parameters = parameters)

        return response?.let { FileImpl(it, this) }
    }

    /**
     * Retrieves all versions of a document.
     *
     * Each snapshot is represented by a [Document] differentiable using its [version][Document.version].
     *
     * @param key         The unique identifier of the document.
     * @param formatter   [Formatter] used to [format][DocumentFile.formatted] files for certain environments.
     * @param styleName   Style to use for formatting, only works in combination with [formatter].
     * @param withContent Whether [DocumentFile.content] should contain the content instead of `null`. (Default: `true`)
     *
     * @throws IllegalArgumentException If [styleName] is specified without [formatter].
     *
     * @return List of [Documents][Document] containing all snapshots of the document.
     */
    suspend fun getDocumentVersions(
        key: String,
        formatter: Formatter? = null,
        styleName: String? = null,
        withContent: Boolean = true
    ): List<Document>? {
        require(!(styleName != null && formatter == null)) { "Setting style name only works when specifying a formatter!" }

        val parameters = parameters {
            formatter?.let { this["formatter"] = it.id }
            styleName?.let { this["style"] = it }
            this["withContent"] = withContent.toString()
        }

        val response: List<ApiDocument>? = request(Route.GET_DOCUMENT_VERSIONS.compile(key), parameters = parameters)

        return response?.let { versions ->
            versions.map { DocumentImpl(it, this) }
        }
    }

    /**
     * Creates a new [Document] on the target Gobin server.
     *
     * @param content   The content of the file.
     * @param language  The language of the file. (Default: [AUTO][Language.AUTO])
     * @param fileName  The name of the file. (Default: `untitled`)
     * @param formatter [Formatter] used to [format][DocumentFile.formatted] files for certain environments.
     * @param styleName Style to use for formatting, only works in combination with [formatter].
     * @param expires   When this document should expire.
     *
     * @throws IllegalArgumentException If [styleName] is specified without [formatter].
     *
     * @return [Document] with a single file.
     */
    suspend fun createDocument(
        content: CharSequence,
        language: Language = Language.AUTO,
        fileName: String = "untitled",
        formatter: Formatter? = null,
        styleName: String? = null,
        expires: Instant? = null,
    ): Document {
        val file = fileInput(fileName, content, language)

        return createDocument(listOf(file), formatter, styleName, expires)
    }

    /**
     * Creates a new [Document] on the target Gobin server.
     *
     * @param files     Collection of [Files][FileData] to include in the document.
     * @param formatter [Formatter] used to [format][DocumentFile.formatted] files for certain environments.
     * @param styleName Style to use for formatting, only works in combination with [formatter].
     * @param expires   When this document should expire.
     *
     * @throws IllegalArgumentException If [styleName] is specified without [formatter].
     *
     * @return [Document] containing all uploaded files.
     */
    suspend fun createDocument(
        files: Collection<FileData>,
        formatter: Formatter? = null,
        styleName: String? = null,
        expires: Instant? = null,
    ): Document {
        require(!(styleName != null && formatter == null)) { "Setting style name only works when specifying a formatter!" }

        val parameters = parameters {
            formatter?.let { this["formatter"] = it.id }
            styleName?.let { this["style"] = it }
            expires?.let { this["expires"] = it.toString() }
        }

        val body = files.buildMultipartBody()
        val response: ApiDocument = request(Route.CREATE_DOCUMENT.compile(), body, parameters = parameters)

        return DocumentImpl(response, this)
    }

    /**
     * Updates a document, replacing its files with a collection of new files.
     *
     * @param key         The unique identifier of the document.
     * @param updateToken The update token needed to update this document. See [Document.updateToken].
     * @param newFiles    Collection of [Files][FileData] to replace the old ones with.
     * @param formatter   [Formatter] used to [format][DocumentFile.formatted] files for certain environments.
     * @param styleName   Style to use for formatting, only works in combination with [formatter].
     *
     * @throws IllegalArgumentException If [styleName] is specified without [formatter].
     *
     * @return [Document] containing the new collection of files.
     */
    suspend fun updateDocument(
        key: String,
        updateToken: String,
        newFiles: Collection<FileData>,
        formatter: Formatter? = null,
        styleName: String? = null,
    ): Document? {
        require(!(styleName != null && formatter == null)) { "Setting style name only works when specifying a formatter!" }

        val headers = headers {
            this[HttpHeaders.Authorization] = "Bearer $updateToken"
        }

        val parameters = parameters {
            formatter?.let { this["formatter"] = it.id }
            styleName?.let { this["style"] = it }
        }

        val body = newFiles.buildMultipartBody()
        val response: ApiDocument? =
            request(Route.UPDATE_DOCUMENT.compile(key), body, headers = headers, parameters = parameters)

        return response?.let { DocumentImpl(it, this) }
    }

    /**
     * Deletes a document.
     *
     * @param key         The unique identifier of the document.
     * @param updateToken The update token needed to update/delete this document. See [Document.updateToken].
     */
    suspend fun deleteDocument(key: String, updateToken: String) =
        deleteDocument(key, updateToken, null)

    /**
     * Deletes a specific document [version][Document.version].
     *
     * @param key         The unique identifier of the document.
     * @param updateToken The update token needed to update this document. See [Document.updateToken].
     * @param version     The [version][Document.version] to delete.
     *
     * @return [Int] representing the amount of document snapshots still available after deletion.
     */
    suspend fun deleteDocumentVersion(key: String, updateToken: String, version: Long) =
        deleteDocument(key, updateToken, version)

    /**
     * Shares a Gobin document.
     *
     * This essentially generates an [update token][Document.updateToken] with access to the provided permissions.
     *
     * **You cannot grant permissions the provided [updateToken] itself does not have!**
     *
     * @param key         The unique identifier of the document.
     * @param updateToken The update token needed to update this document. See [Document.updateToken].
     * @param permissions Collection of [permissions][Permission] the new update token is supposed to have.
     *
     * @return Update token with the specified permissions
     */
    suspend fun shareDocument(key: String, updateToken: String, permissions: Collection<Permission>): String {
        val permissionBody = Permissions(permissions.map { it.id }.toSet())

        val headers = headers {
            this[HttpHeaders.Authorization] = "Bearer $updateToken"
        }

        val response: DocumentShareResponse =
            request(Route.SHARE_DOCUMENT.compile(key), permissionBody, headers = headers)

        return response.token
    }

    fun close() {
        parentJob.cancel()
        httpClient.close()
    }

    private suspend fun deleteDocument(key: String, updateToken: String, version: Long?): Int {
        val headers = headers {
            this[HttpHeaders.Authorization] = "Bearer $updateToken"
        }

        val route = when {
            version != null -> Route.DELETE_DOCUMENT_VERSION.compile(key, version.toString())
            else -> Route.DELETE_DOCUMENT.compile(key)
        }

        val response: RemainingVersions? = request(route, headers = headers)
        return response?.versions ?: 0
    }
}

fun <T : HttpClientEngineConfig> ktbin(
    httpEngine: HttpClientEngineFactory<T>,
    httpConfiguration: HttpClientConfig<T>.() -> Unit = { },
    host: GobinHost = defaultGobinHost,
    coroutineDispatcher: CoroutineDispatcher,
): Ktbin {
    val httpClient = HttpClient(httpEngine, makeHttpConfig(httpConfiguration))
    return Ktbin(host, coroutineDispatcher, httpClient)
}

private typealias HttpConfig<T> = HttpClientConfig<T>.() -> Unit

internal val json = Json {
    ignoreUnknownKeys = true
}

private fun <T : HttpClientEngineConfig> makeHttpConfig(config: HttpConfig<T>): HttpConfig<T> = {
    install(ContentNegotiation) {
        json(json)
    }
    config()
}

internal suspend inline fun <reified T, reified A> Ktbin.request(
    route: Route.CompiledRoute,
    body: T,
    headers: Headers = Headers.Empty,
    parameters: Parameters = Parameters.Empty,
): A = withContext(parentJob + coroutineDispatcher) {
    requester.request(Request(this@request, route, body, typeInfo<T>(), typeInfo<A>(), headers, parameters))
}

internal suspend inline fun <reified A> Ktbin.request(
    route: Route.CompiledRoute,
    headers: Headers = Headers.Empty,
    parameters: Parameters = Parameters.Empty,
): A = withContext(parentJob + coroutineDispatcher) {
    requester.request(Request(this@request, route, Unit, typeInfo<Unit>(), typeInfo<A>(), headers, parameters))
}