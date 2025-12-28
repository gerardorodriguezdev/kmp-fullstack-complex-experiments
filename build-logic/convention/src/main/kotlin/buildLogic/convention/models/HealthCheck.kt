package buildLogic.convention.models

import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property

interface HealthCheck {
    val test: ListProperty<String>
    val interval: Property<String>
    val timeout: Property<String>
    val retries: Property<Int>
}