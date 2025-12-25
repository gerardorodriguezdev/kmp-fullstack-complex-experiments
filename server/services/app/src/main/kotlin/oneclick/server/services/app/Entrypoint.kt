package oneclick.server.services.app

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import oneclick.server.services.app.di.Dependencies
import oneclick.server.services.app.plugins.*
import oneclick.server.services.app.plugins.authentication.configureAuthentication

internal fun server(dependencies: Dependencies): EmbeddedServer<NettyApplicationEngine, NettyApplicationEngine.Configuration> =
    embeddedServer(
        factory = Netty,
        port = 8080,
        module = {
            configureModules(dependencies = dependencies)
        },
    )

private fun Application.configureModules(dependencies: Dependencies) {
    configureCallLogging(timeProvider = dependencies.timeProvider)
    configureSerialization()
    configureSessions(disableSecureCookies = dependencies.disableSecureCookie)
    configureAuthentication(
        invalidJwtDataSource = dependencies.invalidJwtDataSource,
        userJwtProvider = dependencies.userJwtProvider,
        homeJwtProvider = dependencies.homeJwtProvider,
    )
    configureStatusPages()
    configureRequestValidation()
    configureRequestBodyLimit()
    configureRateLimit(disableRateLimit = dependencies.disableRateLimit, timeProvider = dependencies.timeProvider)
    configureCallId(dependencies.uuidProvider)
    configureCompression(dependencies.baseUrl)
    configureShutdown(dependencies.onShutdown)
    configureRouting(
        usersRepository = dependencies.usersRepository,
        passwordManager = dependencies.passwordManager,
        uuidProvider = dependencies.uuidProvider,
        homesRepository = dependencies.homesRepository,
        invalidJwtDataSource = dependencies.invalidJwtDataSource,
        userJwtProvider = dependencies.userJwtProvider,
        homeJwtProvider = dependencies.homeJwtProvider,
        emailService = dependencies.emailService,
        registrationCodeProvider = dependencies.registrationCodeProvider,
        registrableUsersRepository = dependencies.registrableUsersRepository,
    )
    configureCsrf(originUrl = dependencies.baseUrl)
    configureHsts(disableHsts = dependencies.disableHsts)
}