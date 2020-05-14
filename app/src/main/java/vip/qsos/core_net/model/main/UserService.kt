package vip.qsos.core_net.model.main

import android.content.Context
import com.google.gson.Gson
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import vip.qsos.core_net.lib.mock.MockInterceptor
import vip.qsos.core_net.model.HttpResult
import vip.qsos.core_net.model.mock.UserMockData
import java.util.concurrent.TimeUnit

interface UserService {

    companion object {

        private const val ENDPOINT = "http://192.168.2.199:8080/"
        var appContext: Context? = null
        val INSTANCE: UserService by lazy {
            val mClient = OkHttpClient.Builder()
            mClient.connectTimeout(8, TimeUnit.SECONDS)
            appContext?.let {
                val interceptor = MockInterceptor(it)
                interceptor.addMockData(UserMockData())
                mClient.addInterceptor(interceptor)
            }
            Retrofit.Builder()
                .baseUrl(ENDPOINT)
                .callFactory(mClient.build())
                .addConverterFactory(GsonConverterFactory.create(Gson()))
                .build()
                .create(UserService::class.java)
        }
    }

    @GET("/api/user")
    suspend fun getUserInfo(): HttpResult<UserInfo>

}