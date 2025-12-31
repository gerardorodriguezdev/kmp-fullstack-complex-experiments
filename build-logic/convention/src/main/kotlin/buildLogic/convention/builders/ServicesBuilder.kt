package buildLogic.convention.builders

import buildLogic.convention.configurations.DockerComposeConfiguration.Service
import buildLogic.convention.configurations.DockerComposeConfiguration.Service.Companion.IDENTIFIER_KEY
import buildLogic.convention.configurations.DockerComposeConfiguration.Service.Companion.TYPE_KEY
import buildLogic.convention.configurations.DockerComposeConfiguration.Service.HealthCheck

interface ServicesBuilderScope {

    fun postgres(
        imageVersion: Int,
        databaseName: String,
        databaseUsername: String,
        databasePassword: String,
        ports: List<String> = listOf("5432:5432"),
        volumes: List<String> = listOf("/var/lib/postgresql/data"),
    )

    fun redis(
        imageVersion: Int,
        ports: List<String> = listOf("6379:6379"),
        volumes: List<String> = listOf("/data"),
    )

    fun prometheus(
        imageVersion: String,
        ports: List<String> = listOf("9090:9090"),
        volumes: List<String> = listOf("./prometheus.yml:/etc/prometheus/prometheus.yml"),
    )

    fun grafana(
        imageVersion: String,
        ports: List<String> = listOf("3000:3000"),
        volumes: List<String> = listOf("/var/lib/grafana"),
    )
}

internal class ServicesBuilder : ServicesBuilderScope {
    val services = mutableListOf<Service>()

    override fun postgres(
        imageVersion: Int,
        databaseName: String,
        databaseUsername: String,
        databasePassword: String,
        ports: List<String>,
        volumes: List<String>,
    ) {
        services.add(
            Service(
                image = "postgres:$imageVersion",
                ports = ports,
                volumes = volumes,
                environment = buildMap {
                    put("POSTGRES_DB", databaseName)
                    put("POSTGRES_USER", databaseUsername)
                    put("POSTGRES_PASSWORD", databasePassword)
                },
                healthcheck = HealthCheck(
                    test = listOf("CMD-SHELL", "pg_isready -U postgres"),
                    interval = "5s",
                    timeout = "5s",
                    retries = 5,
                ),
                extras = mapOf(
                    IDENTIFIER_KEY to "postgres",
                )
            )
        )
    }

    override fun redis(
        imageVersion: Int,
        ports: List<String>,
        volumes: List<String>,
    ) {
        services.add(
            Service(
                image = "redis:$imageVersion",
                ports = ports,
                volumes = volumes,
                extras = mapOf(
                    IDENTIFIER_KEY to "redis",
                )
            )
        )
    }

    override fun prometheus(
        imageVersion: String,
        ports: List<String>,
        volumes: List<String>,
    ) {
        services.add(
            Service(
                image = "prom/prometheus:$imageVersion",
                ports = ports,
                volumes = volumes,
                extras = mapOf(
                    IDENTIFIER_KEY to "prometheus",
                    TYPE_KEY to "prometheus",
                )
            )
        )
    }

    override fun grafana(
        imageVersion: String,
        ports: List<String>,
        volumes: List<String>,
    ) {
        services.add(
            Service(
                image = "grafana/grafana:$imageVersion",
                ports = ports,
                volumes = volumes,
                extras = mapOf(
                    IDENTIFIER_KEY to "grafana",
                )
            )
        )
    }
}
