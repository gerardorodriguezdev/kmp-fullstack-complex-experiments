package oneclick.client.shared.network

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.compression.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import oneclick.client.shared.network.dataSources.TokenDataSource
import oneclick.client.shared.network.extensions.clientType
import oneclick.client.shared.network.extensions.origin
import oneclick.client.shared.network.platform.LogoutManager
import oneclick.client.shared.network.plugins.LogoutProxy
import oneclick.client.shared.network.plugins.TokenProxy
import oneclick.shared.contracts.core.models.ClientType
import oneclick.shared.logging.AppLogger

fun nativeHttpClient(
    url: Url,
    clientType: ClientType,
    appLogger: AppLogger,
    httpClientEngine: HttpClientEngine,
    tokenDataSource: TokenDataSource,
    logoutManager: LogoutManager,
): HttpClient =
    HttpClient(httpClientEngine) {
        install(ContentNegotiation) {
            json()
        }
        install(DefaultRequest) {
            contentType(ContentType.Application.Json)

            this.url(url.toString())

            clientType(clientType)

            origin(url)
        }

        install(TokenProxy) {
            this.tokenDataSource = tokenDataSource
        }

        install(LogoutProxy) {
            onLogout = logoutManager::logout
        }

        install(Logging) {
            logger = appLogger.toLogger()
            level = LogLevel.ALL
        }

        install(ContentEncoding) {
            gzip()
        }
    }

private fun AppLogger.toLogger(): Logger =
    object : Logger {
        override fun log(message: String) {
            i("AppNetworking", message)
        }
    }
