package mapan.prototype.mapanbacakomik.util

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import mapan.prototype.mapanbacakomik.config.Constants
import okhttp3.*
import okio.*
import java.io.InputStream
import java.util.concurrent.TimeUnit


@GlideModule
open class MyAppGlideModule : AppGlideModule(){
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {

        val client = OkHttpClient.Builder()
        .connectTimeout(Constants.IMG_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
        .readTimeout(Constants.IMG_READ_TIMEOUT, TimeUnit.MILLISECONDS)
//        .writeTimeout(Constants.IMG_WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
//        val factory = OkHttpUrlLoader.Factory(client)
        client.addNetworkInterceptor(object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                val request = chain.request()
                val response = chain.proceed(request)
                val listener = DispatchingProgressListener()
                return response.newBuilder()
                    .body(response.body?.let {
                        OkHttpProgressResponseBody(request.url,
                            it, listener)
                    })
                    .build()
            }
        })
        var clientBuild = client.build()
        val factory = OkHttpUrlLoader.Factory(clientBuild)
        glide.registry.replace(GlideUrl::class.java, InputStream::class.java, factory)

    }

    companion object {
        fun forget(url: String) {
            DispatchingProgressListener.forget(url)
        }

        fun expect(url: String, listener: UIonProgressListener) {
            DispatchingProgressListener.expect(url, listener)
        }

    }
    private interface ResponseProgressListener {
        fun update(url: HttpUrl?, bytesRead: Long, contentLength: Long)
    }

    interface UIonProgressListener {
        fun onProgress(bytesRead: Long, expectedLength: Long)

        /**
         * Control how often the listener needs an update. 0% and 100% will always be dispatched.
         * @return in percentage (0.2 = call [.onProgress] around every 0.2 percent of progress)
         */
        val granualityPercentage: Float
    }
    class DispatchingProgressListener internal constructor() :
        ResponseProgressListener {
        private val handler: Handler

        init {
            handler = Handler(Looper.getMainLooper())
        }

        override fun update(url: HttpUrl?, bytesRead: Long, contentLength: Long) {
            //System.out.printf("%s: %d/%d = %.2f%%%n", url, bytesRead, contentLength, (100f * bytesRead) / contentLength);
            val key = url.toString()
            val listener = LISTENERS[key] ?: return
            if (contentLength <= bytesRead) {
                forget(key)
            }
            if (needsDispatch(key, bytesRead, contentLength, listener.granualityPercentage)) {
                handler.post(Runnable { listener.onProgress(bytesRead, contentLength) })
            }
        }

        private fun needsDispatch(
            key: String,
            current: Long,
            total: Long,
            granularity: Float
        ): Boolean {
            if (granularity == 0f || current == 0L || total == current) {
                return true
            }
            val percent = 100f * current / total
            val currentProgress = (percent / granularity).toLong()
            val lastProgress = PROGRESSES[key]
            return if (lastProgress == null || currentProgress != lastProgress) {
                PROGRESSES[key] = currentProgress
                true
            } else {
                false
            }
        }

        companion object {
            private val LISTENERS: MutableMap<String, UIonProgressListener> = HashMap()
            private val PROGRESSES: MutableMap<String, Long> = HashMap()
            fun forget(url: String) {
                LISTENERS.remove(url)
                PROGRESSES.remove(url)
            }

            fun expect(url: String, listener: UIonProgressListener) {
                LISTENERS[url] = listener
            }
        }
    }
    private class OkHttpProgressResponseBody internal constructor(
        private val url: HttpUrl, private val responseBody: ResponseBody,
        private val progressListener: ResponseProgressListener
    ) : ResponseBody() {
        private var bufferedSource: BufferedSource? = null
        override fun contentType(): MediaType? {
            return responseBody.contentType()
        }

        override fun contentLength(): Long {
            return responseBody.contentLength()
        }

        override fun source(): BufferedSource {
            if (bufferedSource == null) {
                bufferedSource = source(responseBody.source()).buffer()
            }
            return bufferedSource as BufferedSource
        }

        private fun source(source: Source): Source {
            return object : ForwardingSource(source) {
                var totalBytesRead = 0L

                override fun read(sink: Buffer, byteCount: Long): Long {
                    val bytesRead = super.read(sink, byteCount)
                    val fullLength = responseBody.contentLength()
                    if (bytesRead == -1L) { // this source is exhausted
                        totalBytesRead = fullLength
                    } else {
                        totalBytesRead += bytesRead
                    }
                    progressListener.update(url, totalBytesRead, fullLength)
                    return bytesRead
                }
            }
        }
    }
}
