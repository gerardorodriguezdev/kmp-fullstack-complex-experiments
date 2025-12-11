package oneclick.server.services.app.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.hsts.*

internal fun Application.configureHsts() {
    install(HSTS) {
        includeSubDomains = true
    }
}