package oneclick.shared.contracts.auth.models.responses

import kotlinx.serialization.Serializable
import oneclick.shared.contracts.auth.models.Jwt

@Serializable
sealed interface HomeRequestLoginResponse {

    @Serializable
    data class ValidLogin(val jwt: Jwt) : HomeRequestLoginResponse
}