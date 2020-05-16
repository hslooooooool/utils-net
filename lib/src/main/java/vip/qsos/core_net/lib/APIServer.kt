package vip.qsos.core_net.lib

import android.app.Application
import android.text.TextUtils
import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import vip.qsos.core_net.lib.mock.AbstractMockData
import vip.qsos.core_net.lib.mock.MockInterceptor
import java.util.concurrent.TimeUnit

object APIServer {

    /**网络请求接口参数配置
     *
     * 给各个接口增加此配置后，请求服务将优先采用此参数，否者采用通用配置，通用配置见 APIServer.Config
     *
     * @param mockDataList MOCK 数据结合
     * @param timeout 当前接口组超时时长。秒。<= 0 将采用通用配置。
     * @param baseUrl BaseUrl 。为空将采用通用配置。
     *
     * @see APIServer.init
     * @see APIServer.Config
     * */
    data class APIConfig(
        val mockDataList: List<AbstractMockData> = arrayListOf(),
        val timeout: Long = 0L,
        val baseUrl: String? = null
    )

    /**网络请求服务通用配置
     * @param application Application
     * @param baseUrl BaseUrl
     * @param timeout 统一超时时长。秒。默认 8 秒
     * @param interceptor 请求拦截器集合
     * @param retrofitBuilder 选配。不配置采用默认配置。
     * @param okClientBuilder 选配。不配置采用默认配置。
     * */
    data class Config(
        var application: Application?,
        var baseUrl: String?,
        var timeout: Long = 8L,
        val interceptor: List<Interceptor> = arrayListOf(),
        val retrofitBuilder: Retrofit.Builder? = null,
        val okClientBuilder: OkHttpClient.Builder? = null
    )

    private lateinit var mConfig: Config

    /**初始化请求服务*/
    fun init(config: Config) {
        this.mConfig = config
    }

    /**配置请求接口*/
    fun <T> api(cls: Class<T>, config: APIConfig? = null): T {
        val retrofitBuilder: Retrofit.Builder
        val okClientBuilder: OkHttpClient.Builder
        val endPoint: String = when {
            !TextUtils.isEmpty(config?.baseUrl) -> {
                config!!.baseUrl!!
            }
            !TextUtils.isEmpty(mConfig.baseUrl) -> {
                mConfig.baseUrl!!
            }
            else -> {
                throw NullPointerException("endPoint can`t be null")
            }
        }
        if (mConfig.okClientBuilder == null) {
            okClientBuilder = OkHttpClient.Builder()
            var timeout = 8L
            if (config?.timeout != null && config.timeout > 0L) {
                timeout = config.timeout
            } else if (mConfig.timeout > 0L) {
                timeout = mConfig.timeout
            }
            okClientBuilder.connectTimeout(timeout, TimeUnit.SECONDS)
            mConfig.application?.let {
                val interceptor = MockInterceptor(it)
                config?.mockDataList?.forEach { data ->
                    interceptor.addMockData(data)
                }
                if (interceptor.count() > 0) {
                    okClientBuilder.addInterceptor(interceptor)
                }
            }
            mConfig.interceptor.forEach {
                okClientBuilder.addInterceptor(it)
            }
        } else {
            okClientBuilder = mConfig.okClientBuilder!!
        }
        retrofitBuilder = mConfig.retrofitBuilder ?: Retrofit.Builder()
            .baseUrl(endPoint)
            .callFactory(okClientBuilder.build())
            .addConverterFactory(GsonConverterFactory.create(Gson()))
        return retrofitBuilder.build().create(cls)
    }

}