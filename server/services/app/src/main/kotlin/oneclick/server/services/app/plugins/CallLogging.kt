package oneclick.server.services.app.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.callid.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.request.*
import oneclick.shared.timeProvider.TimeProvider
import org.slf4j.event.Level

internal fun Application.configureCallLogging(timeProvider: TimeProvider) {
    install(CallLogging) {
        level = Level.DEBUG
        this.logger = this@configureCallLogging.log

        clock { timeProvider.currentTimeMillis() }

        filter { call -> call.request.path().startsWith("/") }

        callIdMdc("call-id")
    }
}
