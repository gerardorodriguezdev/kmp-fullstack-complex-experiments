package buildLogic.convention.configurations

import kotlinx.serialization.*
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
        @EncodeDefault(EncodeDefault.Mode.NEVER)
        val environment: Map<String, String> = emptyMap(),
        @EncodeDefault(EncodeDefault.Mode.NEVER)
        @SerialName("depends_on")
        val dependsOn: Map<String, Condition> = emptyMap(),
        @EncodeDefault(EncodeDefault.Mode.NEVER)
        val volumes: List<String> = emptyList(),
        @EncodeDefault(EncodeDefault.Mode.NEVER)
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
        data class Condition(val condition: String) : JavaSerializable

        companion object {
            val Service.identifier get() = extras.getValue("identifier")
        }
    }
}