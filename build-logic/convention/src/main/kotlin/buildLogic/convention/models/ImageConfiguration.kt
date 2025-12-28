package buildLogic.convention.models

import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property

interface ImageConfiguration {
    val identifier: Property<String>
    val name: Property<String>
    val tag: Property<String>
    val port: Property<Int>
    val volume: Property<String>
    val dependsOn: MapProperty<String, String>
    val environmentVariables: MapProperty<String, String>
    val healthCheck: Property<HealthCheck>
}