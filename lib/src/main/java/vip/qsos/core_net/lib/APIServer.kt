package vip.qsos.core_net.lib

import android.content.Context
import android.text.TextUtils
import com.google.gson.Gson
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import vip.qsos.core_net.lib.mock.AbstractMockData
import vip.qsos.core_net.lib.mock.MockInterceptor
import java.util.concurrent.TimeUnit

/**全局请求配置*/
interface IAPIServer {
    var appContext: Context?
    var endPoint: String?
    var timeOut: Long?
}

/**请求基础接口*/
interface IApi {
    data class Config(
        val timeOut: Long = 8L,
        val mockDataList: List<AbstractMockData> = arrayListOf(),
        val endPoint: String? = null
    )
}

object APIServer {

    private var mServer: IAPIServer? = null
    private var mServerMap: HashMap<Class<*>, Class<*>> = HashMap()

    fun init(server: IAPIServer) {
        this.mServer = server
    }

    fun <T> api(cls: Class<T>, config: IApi.Config? = null): T {
        if (mServer == null) {
            throw NullPointerException("APIServer must be init!")
        }
        val s1 = mServerMap[cls]
        if (s1 == null) {
            val mClient = OkHttpClient.Builder()
            val timeOut = when {
                mServer!!.timeOut != null -> mServer!!.timeOut!!
                else -> config?.timeOut ?: 8L
            }
            mClient.connectTimeout(timeOut, TimeUnit.SECONDS)
            mServer!!.appContext?.let {
                val interceptor = MockInterceptor(it)
                config?.mockDataList?.forEach { data ->
                    interceptor.addMockData(data)
                }
                if (interceptor.count() > 0) {
                    mClient.addInterceptor(interceptor)
                }
            }
            val endPoint: String = when {
                !TextUtils.isEmpty(mServer!!.endPoint) -> {
                    mServer!!.endPoint!!
                }
                !TextUtils.isEmpty(config?.endPoint) -> {
                    config!!.endPoint!!
                }
                else -> {
                    throw NullPointerException("endPoint can`t be null")
                }
            }
            val s2 = Retrofit.Builder()
                .baseUrl(endPoint)
                .callFactory(mClient.build())
                .addConverterFactory(GsonConverterFactory.create(Gson()))
                .build()
                .create(cls)
            return s2
        }
        return s1 as T
    }

}