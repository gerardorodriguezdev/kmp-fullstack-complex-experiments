package buildLogic.convention.configurations

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import java.io.Serializable as JavaSerializable

interface DockerComposeConfiguration {
    val executablePath: Property<String>
    val services: ListProperty<Service>

    @OptIn(ExperimentalSerializationApi::class)
    @Serializable
    data class Service(
        val image: String,
        val ports: List<String>,
        val environment: Map<String, String> = emptyMap(),
        @SerialName("depends_on")
        val dependsOn: Map<String, Condition> = emptyMap(),
        val volumes: List<String> = emptyList(),
        val healthcheck: HealthCheck? = null,
        @Transient
        val extras: Map<String, String> = emptyMap(),
    ) : JavaSerializable {

        @Serializable
        data class HealthCheck(
            val test: List<String>,
            val interval: String,
            val timeout: String,
            val retries: Int,
        ) : JavaSerializable

        @Serializable
        data class Condition(val condition: ConditionType) : JavaSerializable {
            enum class ConditionType {
                @SerialName("service_healthy")
                SERVICE_HEALTHY,

                @SerialName("service_started")
                SERVICE_STARTED,
            }
        }

        companion object {
            val Service.identifier get() = extras.getValue(IDENTIFIER_KEY)
            const val IDENTIFIER_KEY = "identifier"

            const val TYPE_KEY = "type"
            const val APP_SERVICE_TYPE = "app"

            const val METRICS_PORT_KEY = "metricsPort"
        }
    }
}