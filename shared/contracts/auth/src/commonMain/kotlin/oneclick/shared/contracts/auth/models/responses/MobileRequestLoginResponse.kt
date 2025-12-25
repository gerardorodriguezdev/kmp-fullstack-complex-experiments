package oneclick.shared.contracts.auth.models.responses

import kotlinx.serialization.Serializable
import oneclick.shared.contracts.auth.models.Jwt

@Serializable
sealed interface MobileRequestLoginResponse {

    @Serializable
    data class ValidLogin(val jwt: Jwt) : MobileRequestLoginResponse

    @Serializable
    data object WaitForApproval : MobileRequestLoginResponse
}