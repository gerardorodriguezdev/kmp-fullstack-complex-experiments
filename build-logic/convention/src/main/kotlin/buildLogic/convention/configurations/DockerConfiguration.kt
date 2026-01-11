package buildLogic.convention.configurations

import org.gradle.api.provider.Property

interface DockerConfiguration {
    val executablePath: Property<String>
    val port: Property<Int>
    val healthzPort: Property<Int>
    val metricsPort: Property<Int>
    val name: Property<String>
    val tag: Property<String>
    val registryUrl: Property<String>
    val registryUsername: Property<String>
    val registryPassword: Property<String>
}