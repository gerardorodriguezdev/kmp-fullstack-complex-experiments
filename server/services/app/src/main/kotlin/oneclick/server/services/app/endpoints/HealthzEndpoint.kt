package oneclick.server.services.app.endpoints

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import oneclick.server.services.app.models.ServerEndpoint

internal fun Routing.healthzEndpoint(healthzPort: Int) {
    port(healthzPort) {
        get(ServerEndpoint.HEALTHZ.route) {
            call.respond(HttpStatusCode.OK)
        }
    }
}