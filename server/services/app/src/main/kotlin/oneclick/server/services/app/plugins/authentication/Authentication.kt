package oneclick.server.services.app.plugins.authentication

import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.impl.JWTParser
import com.auth0.jwt.interfaces.DecodedJWT
import com.auth0.jwt.interfaces.Payload
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import oneclick.server.services.app.authentication.AuthenticationType
import oneclick.server.services.app.authentication.HomeJwtProvider
import oneclick.server.services.app.authentication.JwtCredentials.HomeJwtCredentials
import oneclick.server.services.app.authentication.JwtCredentials.UserJwtCredentials
import oneclick.server.services.app.authentication.UserJwtProvider
import oneclick.server.services.app.dataSources.base.InvalidJwtDataSource
import oneclick.server.services.app.plugins.authentication.AuthenticationConstants.JWT_REALM
import oneclick.server.shared.authentication.models.JwtId.Companion.toJwtId
import oneclick.shared.contracts.auth.models.Jwt
import oneclick.shared.contracts.core.models.Uuid
import oneclick.shared.contracts.core.models.Uuid.Companion.toUuid
import java.util.*
import kotlin.io.encoding.ExperimentalEncodingApi

internal fun Application.configureAuthentication(
    invalidJwtDataSource: InvalidJwtDataSource,
    userJwtProvider: UserJwtProvider,
    homeJwtProvider: HomeJwtProvider,
) {
    install(Authentication) {
        AuthenticationType.entries.forEach { authenticationType ->
            when (authenticationType) {
                AuthenticationType.USER_SESSION -> registerUserSessionAuthentication(
                    userJwtProvider = userJwtProvider,
                    invalidJwtDataSource = invalidJwtDataSource,
                )

                AuthenticationType.USER_JWT -> registerUserJwtAuthentication(
                    userJwtProvider = userJwtProvider,
                    invalidJwtDataSource = invalidJwtDataSource,
                )

                AuthenticationType.HOME_JWT -> registerHomeJwtAuthentication(
                    homeJwtProvider = homeJwtProvider,
                    invalidJwtDataSource = invalidJwtDataSource
                )
            }
        }
    }
}

private fun AuthenticationConfig.registerUserSessionAuthentication(
    userJwtProvider: UserJwtProvider,
    invalidJwtDataSource: InvalidJwtDataSource,
) {
    session<Jwt>(AuthenticationType.USER_SESSION.value) {
        validate { jwt ->
            val decodedJwt = try {
                userJwtProvider.jwtVerifier.verify(jwt.value)
            } catch (error: JWTVerificationException) {
                application.log.error("Error decoding jwt", error)
                null
            } ?: return@validate null

            val jtiString = decodedJwt.id ?: return@validate null
            val jti = jtiString.toUuid() ?: return@validate null
            if (invalidJwtDataSource.isJwtInvalid(jti)) return@validate null

            val payload = decodedJwt.parsePayload()
            val jwtCredential = JWTCredential(payload)

            userJwtProvider.userJwtCredentials(jti = jti, jwtCredential = jwtCredential)
        }

        challenge {
            call.application.log.debug("Invalid session")
            call.sessions.clear<Jwt>()
            call.respond(HttpStatusCode.Unauthorized)
        }
    }
}

private fun AuthenticationConfig.registerUserJwtAuthentication(
    userJwtProvider: UserJwtProvider,
    invalidJwtDataSource: InvalidJwtDataSource,
) {
    jwt(AuthenticationType.USER_JWT.value) {
        realm = JWT_REALM

        verifier(userJwtProvider.jwtVerifier)

        validate { jwtCredential ->
            val jtiString = jwtCredential.jwtId ?: return@validate null
            val jti = jtiString.toUuid() ?: return@validate null
            if (invalidJwtDataSource.isJwtInvalid(jti)) return@validate null

            userJwtProvider.userJwtCredentials(jti = jti, jwtCredential = jwtCredential)
        }

        challenge { _, _ ->
            call.application.log.debug("Invalid user jwt")
            call.respond(HttpStatusCode.Unauthorized)
        }
    }
}

private fun AuthenticationConfig.registerHomeJwtAuthentication(
    homeJwtProvider: HomeJwtProvider,
    invalidJwtDataSource: InvalidJwtDataSource,
) {
    jwt(AuthenticationType.HOME_JWT.value) {
        realm = JWT_REALM

        verifier(homeJwtProvider.jwtVerifier)

        validate { jwtCredential ->
            val jtiString = jwtCredential.jwtId ?: return@validate null
            val jti = jtiString.toUuid() ?: return@validate null
            if (invalidJwtDataSource.isJwtInvalid(jti)) return@validate null

            homeJwtProvider.homeJwtCredentials(jti = jti, jwtCredential = jwtCredential)
        }

        challenge { _, _ ->
            call.application.log.debug("Invalid home jwt")
            call.respond(HttpStatusCode.Unauthorized)
        }
    }
}


private fun UserJwtProvider.userJwtCredentials(
    jti: Uuid,
    jwtCredential: JWTCredential,
): UserJwtCredentials? {
    val userIdString = jwtCredential.payload.getClaim(UserJwtProvider.USER_ID_CLAIM).asString()
    val userJwtId = userIdString.toJwtId() ?: return null
    val userId = id(userJwtId) ?: return null

    return UserJwtCredentials(jti = jti, userId = userId)
}

private fun HomeJwtProvider.homeJwtCredentials(
    jti: Uuid,
    jwtCredential: JWTCredential,
): HomeJwtCredentials? {
    val userIdString = jwtCredential.payload.getClaim(HomeJwtProvider.USER_ID_CLAIM).asString()
    val userJwtId = userIdString.toJwtId() ?: return null
    val userId = id(userJwtId) ?: return null

    val homeIdString = jwtCredential.payload.getClaim(HomeJwtProvider.HOME_ID_CLAIM).asString()
    val homeJwtId = homeIdString.toJwtId() ?: return null
    val homeId = id(homeJwtId) ?: return null

    return HomeJwtCredentials(jti = jti, userId = userId, homeId = homeId)
}

@OptIn(ExperimentalEncodingApi::class)
private fun DecodedJWT.parsePayload(): Payload {
    val payloadString = String(Base64.getDecoder().decode(payload))
    return JWTParser().parsePayload(payloadString)
}

internal object AuthenticationConstants {
    const val JWT_REALM = "OneClick api"
}