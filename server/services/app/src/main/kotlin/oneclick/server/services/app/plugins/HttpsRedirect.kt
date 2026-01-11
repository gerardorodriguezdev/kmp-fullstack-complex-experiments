package oneclick.server.services.app.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.httpsredirect.*

internal fun Application.configureHttpsRedirect(disableHttpsRedirect: Boolean) {
    if (!disableHttpsRedirect) {
        install(HttpsRedirect) {
            permanentRedirect = true
        }
    }
}