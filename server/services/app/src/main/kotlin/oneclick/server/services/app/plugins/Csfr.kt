package oneclick.server.services.app.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.csrf.*

internal fun Application.configureCsrf(allowLocalOrigins: Boolean) {
    install(CSRF) {
        originMatchesHost()

        if (allowLocalOrigins) {
            allowOrigin("localhost")
            allowOrigin("127.0.0.1")
            allowOrigin("0.0.0.0")
            allowOrigin("10.0.2.2")
        }
    }
}