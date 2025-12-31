package buildLogic.convention.tasks

import buildLogic.convention.configurations.DockerComposeConfiguration.Service
import buildLogic.convention.configurations.DockerComposeConfiguration.Service.Companion.APP_SERVICE_TYPE
import buildLogic.convention.configurations.DockerComposeConfiguration.Service.Companion.METRICS_PORT_KEY
import buildLogic.convention.configurations.DockerComposeConfiguration.Service.Companion.TYPE_KEY
import buildLogic.convention.configurations.DockerComposeConfiguration.Service.Companion.identifier
import buildLogic.convention.tasks.CreateDockerComposeConfigTask.PrometheusFile.GlobalConfig
import buildLogic.convention.tasks.CreateDockerComposeConfigTask.PrometheusFile.ScrapeConfig
import buildLogic.convention.tasks.CreateDockerComposeConfigTask.PrometheusFile.ScrapeConfig.StaticConfig
import com.charleskorn.kaml.SequenceStyle
import com.charleskorn.kaml.SingleLineStringStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

@CacheableTask
abstract class CreateDockerComposeConfigTask : DefaultTask() {

    @get:Input
    abstract val servicesList: ListProperty<Service>

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @TaskAction
    fun createFile() {
        val outputDirectory = outputDirectory.get()

        val services = servicesList.get()

        val dockerComposeFileContent = dockerComposeFileContent(services)
        val dockerComposeFile = outputDirectory.file(DOCKER_COMPOSE_FILE_NAME).asFile
        dockerComposeFile.writeText(dockerComposeFileContent)

        val hasPrometheusService = services.hasPrometheusService()
        if (hasPrometheusService) {
            val appService = services.first { service -> service.extras[TYPE_KEY] == APP_SERVICE_TYPE }
            val prometheusFileContent = prometheusFileContent(
                imageName = appService.identifier,
                metricsPort = appService.extras.getValue(METRICS_PORT_KEY).toInt()
            )
            val prometheusFile = outputDirectory.file(PROMETHEUS_FILE_NAME).asFile
            prometheusFile.writeText(prometheusFileContent)
        }
    }

    companion object {
        const val DOCKER_COMPOSE_FILE_NAME = "docker-compose.yml"
        const val PROMETHEUS_FILE_NAME = "prometheus.yml"

        private val yaml = Yaml(
            configuration = YamlConfiguration(
                encodeDefaults = false,
                sequenceStyle = SequenceStyle.Flow,
                singleLineStringStyle = SingleLineStringStyle.PlainExceptAmbiguous,
            )
        )

        internal fun dockerComposeFileContent(services: List<Service>): String {
            val volumes = mutableMapOf<String, String?>()

            return yaml.encodeToString(
                DockerComposeFile.serializer(),
                DockerComposeFile(
                    services = buildMap {
                        services.forEach { service ->
                            val serviceIdentifier = service.identifier
                            volumes[serviceIdentifier] = null

                            put(
                                key = serviceIdentifier,
                                value = service,
                            )
                        }
                    },
                    volumes = volumes,
                )
            )
        }

        internal fun prometheusFileContent(imageName: String, metricsPort: Int): String =
            yaml.encodeToString(
                PrometheusFile.serializer(),
                PrometheusFile(
                    global = GlobalConfig(
                        scrapeInterval = "15s",
                    ),
                    scrapeConfigs = listOf(
                        ScrapeConfig(
                            jobName = "$imageName-scrape-job",
                            metricsPath = "/metrics",
                            staticConfigs = listOf(
                                StaticConfig(
                                    targets = listOf("$imageName:$metricsPort"),
                                )
                            )
                        )
                    ),
                )
            )

        internal fun List<Service>.hasPrometheusService(): Boolean =
            any { service -> service.extras[TYPE_KEY] == "prometheus" }
    }

    @Serializable
    private data class DockerComposeFile(
        val services: Map<String, Service>,
        val volumes: Map<String, String?>,
    )

    @Serializable
    private data class PrometheusFile(
        val global: GlobalConfig,
        @SerialName("scrape_configs")
        val scrapeConfigs: List<ScrapeConfig>,
    ) {
        @Serializable
        data class GlobalConfig(
            @SerialName("scrape_interval")
            val scrapeInterval: String,
        )

        @Serializable
        data class ScrapeConfig(
            @SerialName("job_name")
            val jobName: String,
            @SerialName("metrics_path")
            val metricsPath: String,
            @SerialName("static_configs")
            val staticConfigs: List<StaticConfig>,
        ) {
            @Serializable
            data class StaticConfig(
                val targets: List<String>,
            )
        }
    }
}