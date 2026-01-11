package oneclick.server.services.app.models

internal enum class ServerEndpoint(val route: String) {
    HEALTHZ("/healthz"),
    PROMETHEUS_METRICS("/metrics"),
}