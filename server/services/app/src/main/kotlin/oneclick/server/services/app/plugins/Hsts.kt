package oneclick.server.services.app.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.hsts.*

internal fun Application.configureHsts(disableHsts: Boolean) {
    if (!disableHsts) {
        install(HSTS) {
            includeSubDomains = true
        }
    }
}