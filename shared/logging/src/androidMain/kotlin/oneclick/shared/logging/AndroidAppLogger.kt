package oneclick.shared.logging

import android.util.Log
import oneclick.shared.logging.AppLogger.Companion.DEFAULT_TAG

class AndroidAppLogger : AppLogger {
    override fun i(message: String) {
        Log.i(DEFAULT_TAG, message)
    }

    override fun i(tag: String, message: String) {
        Log.i(tag, message)
    }

    override fun e(message: String) {
        Log.e(DEFAULT_TAG, message)
    }

    override fun e(tag: String, message: String) {
        Log.e(tag, message)
    }
}

actual fun appLogger(): AppLogger = AndroidAppLogger()
