package mapan.prototype.mapanbacakomik.api

import android.content.Context
import com.google.gson.GsonBuilder
import io.realm.Realm
import mapan.prototype.mapanbacakomik.R
import mapan.prototype.mapanbacakomik.config.Constants
import mapan.prototype.mapanbacakomik.util.Log
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.LoggingEventListener
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.InetAddress
import java.net.Proxy
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

class RetrofitClient {

    companion object {
        private var retrofit: Retrofit? = null
        private val httpClient = OkHttpClient.Builder()

        //        private val CACHE_SIZE_BYTES: Long = 1024 * 1024 * 2
        private val CACHE_SIZE_BYTES: Long = 1024 * 1024 * 5
        var retry = 0
        var request_count = 0

//        var basUrlShinigamiId = "https://shinigami.id/wp-admin/"
//        var basUrlShinigamiId = "https://shinigami.ae/wp-admin/"

        fun getClient(context: Context?): Retrofit? {
            var sourceUrls = context?.resources?.getStringArray(R.array.source_website_url)
//            var basUrlShinigamiId = "https://shinigami.sh/wp-admin/"
            var basUrlShinigamiId = sourceUrls!![3]+"wp-admin/"
            if (Log.LOG) {
                val logging = HttpLoggingInterceptor()
                logging.level = HttpLoggingInterceptor.Level.BODY
                httpClient.addInterceptor(logging)
            }
            // set Accept-Language header
            httpClient.addInterceptor(Interceptor { chain ->
                try {
//                    retry++
                    request_count++
//                    Log.e("RequestEk", " => count : " + request_count + " " + chain.request().url)

                    val request =
                        chain.request().newBuilder().addHeader("Accept-Language", "id-ID")
                            .build()

                    return@Interceptor chain.proceed(request)

                } catch (sto: SocketTimeoutException) {
//                    if(listener != null){
//                        listener!!.onConnectionTimeout()
//                    }else{
//
//                    }
//                    return@Interceptor chain.proceed(chain.request())
//                    throw SocketTimeoutException()
//                    Log.e(
//                        "TimeoutEk",
//                        " => count : " + retry + " " + sto.message.toString() + " " + chain.request().url
//                    )
                    if (retry == 3) {
                        retry = 0
                        throw SocketTimeoutException()
                    } else {
                        retry++
                        val request =
                            chain.request().newBuilder().addHeader("Accept-Language", "id-ID")
                                .build()
                        return@Interceptor chain.proceed(request)
                    }
                }
            })

            var clientRetrofit = retrofit

            if (clientRetrofit == null) {
                if (context != null) {
//                    var printListener = PrintingEventListener()
//                    httpClient.eventListener(printListener)

                    if (Log.LOG) {
                        val loggingListener = LoggingEventListener.Factory()
                        httpClient.eventListenerFactory(loggingListener)
                    }

                    httpClient.cache(Cache(context.cacheDir, CACHE_SIZE_BYTES))
                    httpClient.connectTimeout(Constants.CONNECT_TIMEOUT, TimeUnit.SECONDS)
                        .writeTimeout(Constants.WRITE_TIMEOUT, TimeUnit.SECONDS)
                        .readTimeout(Constants.READ_TIMEOUT, TimeUnit.SECONDS);
                }
                val gson = GsonBuilder().setLenient().create()
                clientRetrofit = Retrofit.Builder()
                    .baseUrl(basUrlShinigamiId)
                    //.client(new OkHttpClient.Builder().connectTimeout(0, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS).addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)).build())
                    .client(httpClient.build())
                    .addConverterFactory(GsonConverterFactory.create(gson))
//                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()

                retrofit = clientRetrofit
            }
            return clientRetrofit
        }
    }
}
