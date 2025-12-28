package buildLogic.convention.extensions.plugins

import buildLogic.convention.models.DockerComposeConfiguration
import buildLogic.convention.models.DockerConfiguration
import buildLogic.convention.models.HealthCheck
import buildLogic.convention.models.ImageConfiguration
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.newInstance
import javax.inject.Inject

open class JvmServerExtension @Inject constructor(private val objects: ObjectFactory) {
    val jvmTarget: Property<Int> = objects.property(Int::class.java)
    val mainClass: Property<String> = objects.property(String::class.java)
    val dockerConfiguration: DockerConfiguration = objects.newInstance(DockerConfiguration::class)
    val dockerComposeConfiguration: DockerComposeConfiguration = objects.newInstance(DockerComposeConfiguration::class)

    fun dockerConfiguration(configure: DockerConfiguration.() -> Unit) {
        dockerConfiguration.configure()
    }

    fun dockerComposeConfiguration(configure: DockerComposeConfiguration.() -> Unit) {
        dockerComposeConfiguration.configure()
    }

    fun DockerComposeConfiguration.postgres(
        imageVersion: Provider<Int>,
        databaseName: Provider<String>,
        databaseUsername: Provider<String>,
        databasePassword: Provider<String>,
        port: Provider<Int> = objects.property(Int::class.java).convention(5432),
        volume: Provider<String> = objects.property(String::class.java).convention("/var/lib/postgresql/data"),
    ) {
        val imageConfiguration = objects.newInstance(ImageConfiguration::class)
        dockerComposeConfiguration.imagesConfigurations.add(
            imageConfiguration.apply {
                identifier.set("postgres")
                name.set("postgres")
                tag.set(imageVersion.map { imagesVersion -> imagesVersion.toString() })
                this.port.set(port)
                this.volume.set(volume)
                environmentVariables.put("POSTGRES_DB", databaseName)
                environmentVariables.put("POSTGRES_USER", databaseUsername)
                environmentVariables.put("POSTGRES_PASSWORD", databasePassword)

                this.healthCheck.set(postgresHealthCheck())
            }
        )
    }

    private fun postgresHealthCheck(): HealthCheck {
        val healthCheck = objects.newInstance(HealthCheck::class)
        healthCheck.test.set(listOf("CMD-SHELL", "pg_isready -U postgres"))
        healthCheck.interval.set("5s")
        healthCheck.timeout.set("5s")
        healthCheck.retries.set(5)
        return healthCheck
    }

    fun DockerComposeConfiguration.redis(
        imageVersion: Provider<Int>,
        port: Provider<Int> = objects.property(Int::class.java).convention(6379),
        volume: Provider<String> = objects.property(String::class.java).convention("/data"),
    ) {
        val imageConfiguration = objects.newInstance(ImageConfiguration::class)
        dockerComposeConfiguration.imagesConfigurations.add(
            imageConfiguration.apply {
                identifier.set("redis")
                name.set("redis")
                tag.set(imageVersion.get().toString())
                this.port.set(port)
                this.volume.set(volume)
            }
        )
    }

    fun DockerComposeConfiguration.prometheus(
        imageVersion: Provider<String>,
        port: Provider<Int> = objects.property(Int::class.java).convention(9090),
        volume: Provider<String> = objects.property(String::class.java).convention("/etc/prometheus/"),
    ) {
        val imageConfiguration = objects.newInstance(ImageConfiguration::class)
        dockerComposeConfiguration.imagesConfigurations.add(
            imageConfiguration.apply {
                identifier.set("prometheus")
                name.set("prom/prometheus")
                tag.set(imageVersion)
                this.port.set(port)
                this.volume.set(volume)
            }
        )
    }

    fun DockerComposeConfiguration.grafana(
        imageVersion: Provider<String>,
        port: Provider<Int> = objects.property(Int::class.java).convention(3000),
        volume: Provider<String> = objects.property(String::class.java).convention("/var/lib/grafana"),
    ) {
        val imageConfiguration = objects.newInstance(ImageConfiguration::class)
        dockerComposeConfiguration.imagesConfigurations.add(
            imageConfiguration.apply {
                identifier.set("grafana")
                name.set("grafana/grafana")
                tag.set(imageVersion)
                this.port.set(port)
                this.volume.set(volume)
            }
        )
    }
}
