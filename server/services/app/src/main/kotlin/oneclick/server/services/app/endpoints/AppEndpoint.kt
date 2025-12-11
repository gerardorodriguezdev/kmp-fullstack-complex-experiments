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
                    "default-src 'none'; script-src 'self' 'unsafe-eval'; connect-src 'self'; img-src 'self'; style-src 'self'; frame-ancestors 'none'; require-trusted-types-for 'script';"
                )
                call.response.headers.append(
                    "Cross-Origin-Opener-Policy",
                    "same-origin"
                )
                call.response.headers.append(
                    "X-Content-Type-Options",
                    "nosniff"
                )
                call.response.headers.append(
                    "Permissions-Policy",
                    "accelerometer=(), ambient-light-sensor=(), autoplay=(), battery=(), camera=(), cross-origin-isolated=(), display-capture=(), document-domain=(), encrypted-media=(), execution-while-not-rendered=(), execution-while-out-of-viewport=(), fullscreen=(), geolocation=(), gyroscope=(), keyboard-map=(), magnetometer=(), microphone=(), midi=(), navigation-override=(), payment=(), picture-in-picture=(), publickey-credentials-get=(), screen-wake-lock=(), sync-xhr=(), usb=(), web-share=(), xr-spatial-tracking=(), clipboard-read=(), clipboard-write=(), gamepad=(), speaker-selection=(), conversion-measurement=(), focus-without-user-activation=(), hid=(), idle-detection=(), interest-cohort=(), serial=(), sync-script=(), trust-token-redemption=(), unload=(), window-placement=(), vertical-scroll=()"
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