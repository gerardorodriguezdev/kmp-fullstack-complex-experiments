package oneclick.client.apps.user.di

import io.ktor.client.engine.*
import io.ktor.http.*
import oneclick.client.apps.user.navigation.NavigationController
import oneclick.client.apps.user.notifications.NotificationsController
import oneclick.client.shared.network.dataSources.RemoteAuthenticationDataSource
import oneclick.client.shared.network.dataSources.TokenDataSource
import oneclick.client.shared.network.nativeHttpClient
import oneclick.client.shared.network.platform.LogoutManager
import oneclick.shared.contracts.core.models.ClientType
import oneclick.shared.dispatchers.platform.DispatchersProvider
import oneclick.shared.logging.AppLogger

fun iosCoreComponent(
    url: Url,
    httpClientEngine: HttpClientEngine,
    tokenDataSource: TokenDataSource,
    appLogger: AppLogger,
    dispatchersProvider: DispatchersProvider,
    navigationController: NavigationController,
    logoutManager: LogoutManager,
    notificationsController: NotificationsController,
): CoreComponent {
    val httpClient = nativeHttpClient(
        url = url,
        clientType = ClientType.MOBILE,
        appLogger = appLogger,
        httpClientEngine = httpClientEngine,
        tokenDataSource = tokenDataSource,
        logoutManager = logoutManager,
    )

    return createCoreComponent(
        appLogger = appLogger,
        dispatchersProvider = dispatchersProvider,
        navigationController = navigationController,
        httpClient = httpClient,
        authenticationDataSource = RemoteAuthenticationDataSource(
            httpClient,
            dispatchersProvider,
            tokenDataSource,
            appLogger
        ),
        notificationsController = notificationsController,
    )
}
