package oneclick.server.services.app.plugins

import io.ktor.server.application.*
import io.ktor.server.metrics.micrometer.*
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry

internal fun Application.configureMicrometer(prometheusMeterRegistry: PrometheusMeterRegistry) {
    install(MicrometerMetrics) {
        registry = prometheusMeterRegistry
    }
}