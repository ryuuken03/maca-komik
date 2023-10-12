package mapan.prototype.mapanbacakomik.util

import android.util.Log
import mapan.prototype.mapanbacakomik.config.Constants
import java.lang.Exception

class Log {
    companion object {
        val LOG: Boolean = Constants.IS_LOG

        fun i(tag: String?, string: String?) {
            if (LOG) Log.i(tag, string + "")
        }

        fun e(tag: String?, string: String?) {
            if (LOG) Log.e(tag, string + "")
        }

        fun e(tag: String?, string: String?, tr: Throwable?) {
            if (LOG) Log.e(tag, string, tr)
        }

        fun d(tag: String?, string: String?) {
            if (LOG) Log.d(tag, string + "")
        }

        fun v(tag: String?, string: String?) {
            if (LOG) Log.v(tag, string + "")
        }

        fun w(tag: String?, string: String?, exception: Exception?) {
            if (LOG) Log.w(tag, string + "")
        }
    }

}