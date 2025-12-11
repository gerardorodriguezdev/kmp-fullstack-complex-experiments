package oneclick.server.services.app.endpoints

import io.ktor.http.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import oneclick.server.services.app.endpoints.AppEndpointConstants.fileNameContainsHashRegex
import oneclick.server.services.app.endpoints.AppEndpointConstants.isHtmlFileRegex

internal fun Route.appEndpoint() {
    staticResources("/", "static") {
        preCompressed(CompressedFileType.BROTLI)

        cacheControl { resource ->
            val fileName = resource.file.substringAfterLast('/')
            val shouldCacheFile = fileName.matches(fileNameContainsHashRegex)

            if (shouldCacheFile) {
                listOf(
                    CacheControl.MaxAge(
                        visibility = CacheControl.Visibility.Public,
                        maxAgeSeconds = Int.MAX_VALUE
                    )
                )
            } else {
                emptyList()
            }
        }

        modify { resource, call ->
            val fileName = resource.file.substringAfterLast('/')
            val isHtmlFile = fileName.matches(isHtmlFileRegex)
            if (isHtmlFile) {
                call.response.headers.append(
                    "Content-Security-Policy",
                    "default-src 'none'; script-src 'self' 'unsafe-eval'; connect-src 'self'; img-src 'self'; style-src 'self';"
                )
                call.response.headers.append(
                    "X-Content-Type-Options",
                    "nosniff"
                )
                call.response.headers.append(
                    "Permissions-Policy",
                    "*=()"
                )
            }
        }
    }
}

private object AppEndpointConstants {
    val fileNameContainsHashRegex = Regex(
        ".*[a-f0-9]{8,20}.*",
        RegexOption.IGNORE_CASE
    )
    val isHtmlFileRegex = Regex(
        """\b\w+\.html(\.br)?$"""
    )
}