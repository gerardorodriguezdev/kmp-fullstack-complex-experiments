package oneclick.server.services.app.endpoints

import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import oneclick.server.services.app.models.ServerEndpoint

internal fun Route.prometheusMetricsEndpoint(
    metricsPort: Int,
    prometheusMeterRegistry: PrometheusMeterRegistry
) {
    port(metricsPort) {
        get(ServerEndpoint.PROMETHEUS_METRICS.route) {
            call.respond(prometheusMeterRegistry.scrape())
        }
    }
}