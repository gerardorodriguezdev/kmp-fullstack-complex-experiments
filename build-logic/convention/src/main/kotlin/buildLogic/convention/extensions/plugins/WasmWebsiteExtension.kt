package buildLogic.convention.extensions.plugins

import buildLogic.convention.configurations.WebpackConfiguration
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.newInstance
import javax.inject.Inject

open class WasmWebsiteExtension @Inject constructor(objects: ObjectFactory) {
    val webpackConfiguration: WebpackConfiguration = objects.newInstance(WebpackConfiguration::class)

    fun webpackConfiguration(configure: WebpackConfiguration.() -> Unit) {
        webpackConfiguration.configure()
    }
}
