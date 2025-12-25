package oneclick.server.services.app.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.compression.*

internal fun Application.configureCompression() {
    install(Compression)
}
