package buildLogic.convention.tasks.createDockerComposeConfigTask

import buildLogic.convention.models.ImageConfiguration
import com.charleskorn.kaml.SequenceStyle
import com.charleskorn.kaml.SingleLineStringStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

@CacheableTask
abstract class CreateDockerComposeConfigTask : DefaultTask() {

    @get:Input
    abstract val imagesConfigurations: ListProperty<ImageConfiguration>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun createFile() {
        val outputFile = outputFile.get().asFile
        outputFile.writeText(
            dockerComposeConfigContent(
                imagesConfigurations = imagesConfigurations.get(),
            )
        )
    }

    private companion object {
        val yaml = Yaml(
            configuration = YamlConfiguration(
                sequenceStyle = SequenceStyle.Flow,
                singleLineStringStyle = SingleLineStringStyle.PlainExceptAmbiguous,
            )
        )

        private fun dockerComposeConfigContent(imagesConfigurations: List<ImageConfiguration>): String {
            val volumes = mutableMapOf<String, String?>()

            return yaml.encodeToString(
                DockerComposeFile.serializer(),
                DockerComposeFile(
                    services = buildMap {
                        imagesConfigurations.forEach { imageConfiguration ->
                            val identifier = imageConfiguration.identifier.get()
                            val name = imageConfiguration.name.get()
                            val tag = imageConfiguration.tag.get()
                            val port = imageConfiguration.port.get()
                            val environment = imageConfiguration.environmentVariables.get()
                            val dependsOn = imageConfiguration.dependsOn.get()
                            val volume = imageConfiguration.volume.orNull

                            volumes[identifier] = null

                            put(
                                key = identifier,
                                value = DockerComposeFile.Service(
                                    image = image(imageName = name, imageTag = tag),
                                    ports = ports(port = port),
                                    environment = environment,
                                    dependsOn = dependsOn,
                                    volumes = volume?.let { listOf(volume) } ?: emptyList(),
                                )
                            )
                        }
                    },
                    volumes = volumes,
                )
            )
        }

        private fun image(imageName: String, imageTag: String): String = "$imageName:$imageTag"

        private fun ports(port: Int): List<String> = listOf("$port:$port")
    }

    @Serializable
    private data class DockerComposeFile(
        val services: Map<String, Service>,
        val volumes: Map<String, String?>,
    ) {
        @Serializable
        data class Service(
            val image: String,
            val ports: List<String>,
            val environment: Map<String, String> = emptyMap(),
            @SerialName("depends_on")
            val dependsOn: List<String> = emptyList(),
            val volumes: List<String> = emptyList(),
        )
    }
}
