package vip.qsos.core_net.model

import android.content.Context
import com.google.gson.Gson
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import vip.qsos.core_net.lib.mock.MockInterceptor
import java.util.concurrent.TimeUnit

interface UserService {

    companion object {

        private const val BASE_URL = "http://192.168.2.199:8080/"

        var appContext: Context? = null
        var timeout: Long = 8000L

        val INSTANCE: UserService by lazy {
            val mClient = OkHttpClient.Builder()
            mClient.connectTimeout(timeout, TimeUnit.MILLISECONDS)
            appContext?.let {
                val interceptor = MockInterceptor(it)
                interceptor.addMockData(UserListMockData())
                mClient.addInterceptor(interceptor)
            }
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .callFactory(mClient.build())
                .addConverterFactory(GsonConverterFactory.create(Gson()))
                .build()
                .create(UserService::class.java)
        }

    }

    @GET("/api/user/list")
    suspend fun getUserList(): HttpResult<List<UserInfo>>

}