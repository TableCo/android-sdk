package co.table.sdk.android.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

internal class ApiClient {

    fun getRetrofitObject(workspaceUrl:String?, authToken: String?): ApiInterface {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
            .connectTimeout(100, TimeUnit.SECONDS)
            .readTimeout(100, TimeUnit.SECONDS)
            .writeTimeout(100, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
        if (authToken != null) {
            client.addInterceptor(object : Interceptor {
                override fun intercept(it: Interceptor.Chain): Response {
                    val original = it.request()
                    val request = original.newBuilder()
                        .header("Authorization", "$authToken")
                        .method(original.method, original.body)
                        .build()
                    return it.proceed(request)
                }
            })
        }
        val retrofit = Retrofit.Builder()
            .baseUrl(workspaceUrl)
            .client(client.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(ApiInterface::class.java)
    }

}
