package buildLogic.convention.extensions.plugins

import buildLogic.convention.configurations.DockerConfiguration
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.newInstance
import javax.inject.Inject

open class JvmAppExtension @Inject constructor(objects: ObjectFactory) {
    val jvmTarget: Property<Int> = objects.property(Int::class.java)
    val mainClass: Property<String> = objects.property(String::class.java)
    val dockerConfiguration: DockerConfiguration = objects.newInstance(DockerConfiguration::class)

    fun dockerConfiguration(configure: DockerConfiguration.() -> Unit) {
        dockerConfiguration.configure()
    }
}
