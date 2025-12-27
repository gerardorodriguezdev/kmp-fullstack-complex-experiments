package oneclick.server.services.app

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
            configureCallId(uuidProvider = dependencies.uuidProvider)
            configureCompression()
            configureShutdown(onShutdown = dependencies.onShutdown)
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
            configureCsrf(allowLocalOrigins = dependencies.allowLocalOrigins)
            configureHsts(disableHsts = dependencies.disableHsts)
            configureMicrometer(prometheusMeterRegistry = dependencies.prometheusMeterRegistry)
        },
    )