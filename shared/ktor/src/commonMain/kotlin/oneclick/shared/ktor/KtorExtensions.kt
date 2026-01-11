package oneclick.shared.ktor

import io.ktor.http.*

val HttpHeaders.ClientType: String
    get() = "X-Client-Type"