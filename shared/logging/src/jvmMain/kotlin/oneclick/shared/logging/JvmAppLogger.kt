package oneclick.shared.logging

import oneclick.shared.logging.AppLogger.Companion.DEFAULT_TAG

class JvmAppLogger : AppLogger {
    override fun i(message: String) {
        println("$DEFAULT_TAG $message")
    }

    override fun i(tag: String, message: String) {
        println("[$tag] $message")
    }

    override fun e(message: String) {
        System.err.println("$DEFAULT_TAG $message")
    }

    override fun e(tag: String, message: String) {
        System.err.println("[$tag] $message")
    }
}

actual fun appLogger(): AppLogger = JvmAppLogger()
