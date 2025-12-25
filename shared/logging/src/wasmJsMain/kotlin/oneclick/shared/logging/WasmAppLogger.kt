package oneclick.shared.logging

import oneclick.shared.logging.AppLogger.Companion.DEFAULT_TAG

class WasmAppLogger : AppLogger {
    override fun i(message: String) {
        println("$DEFAULT_TAG $message")
    }

    override fun i(tag: String, message: String) {
        println("[$tag] $message")
    }

    override fun e(message: String) {
        println("🔴$DEFAULT_TAG $message")
    }

    override fun e(tag: String, message: String) {
        println("🔴$DEFAULT_TAG $message")
    }
}

actual fun appLogger(): AppLogger = WasmAppLogger()
