package oneclick.shared.logging

import platform.Foundation.NSLog
import oneclick.shared.logging.AppLogger.Companion.DEFAULT_TAG

class IOSAppLogger : AppLogger {
    override fun i(message: String) {
        NSLog("$DEFAULT_TAG $message")
    }

    override fun i(tag: String, message: String) {
        NSLog("[$tag] $message")
    }

    override fun e(message: String) {
        NSLog("🔴$DEFAULT_TAG $message")
    }

    override fun e(tag: String, message: String) {
        NSLog("🔴$DEFAULT_TAG $message")
    }
}

actual fun appLogger(): AppLogger = IOSAppLogger()
