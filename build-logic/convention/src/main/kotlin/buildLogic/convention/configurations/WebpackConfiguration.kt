package buildLogic.convention.configurations

import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property

interface WebpackConfiguration {
    val port: Property<Int>
    val proxy: Property<Proxy>
    val ignoredFiles: ListProperty<String>

    data class Proxy(
        val context: MutableList<String>,
        val target: String,
    )
}