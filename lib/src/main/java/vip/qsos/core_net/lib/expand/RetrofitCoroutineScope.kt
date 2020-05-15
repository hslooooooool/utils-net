package vip.qsos.core_net.lib.expand

import kotlinx.coroutines.CoroutineScope
import vip.qsos.core_net.lib.callback.HttpLiveData
import vip.qsos.core_net.lib.callback.HttpStatus
import vip.qsos.core_net.lib.callback.IBaseResult
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException

/**
 * @author : 华清松
 * Kotlin 协程配置 Retrofit 请求处理逻辑
 */
class RetrofitCoroutineScope {

    class Dsl<ResultType> : AbsDsl<ResultType>() {
        lateinit var request: suspend () -> ResultType?
        fun request(request: suspend () -> ResultType?) {
            this.request = request
        }
    }

    class DslWithBaseResult<ResultType> : AbsDsl<ResultType>() {
        lateinit var request: suspend () -> IBaseResult<ResultType>?
        fun request(request: suspend () -> IBaseResult<ResultType>?) {
            this.request = request
        }
    }

    class DslWithLiveData<ResultType> {
        lateinit var liveData: (HttpLiveData<ResultType?>)
        lateinit var request: suspend () -> ResultType?

        fun request(request: suspend () -> ResultType?) {
            this.request = request
        }
    }

    abstract class AbsDsl<ResultType> {

        /**请求开始*/
        var onStart: (() -> Unit?)? = null
            private set

        /**请求成功*/
        var onSuccess: ((ResultType?) -> Unit)? = null
            private set

        /**请求完成*/
        var onComplete: (() -> Unit?)? = null
            private set

        /**请求失败*/
        var onFailed: ((code: Int, msg: String, error: Throwable?) -> Unit?)? = null
            private set

        open fun onStart(block: () -> Unit) {
            this.onStart = block
        }

        open fun onSuccess(block: (ResultType?) -> Unit) {
            this.onSuccess = block
        }

        open fun onComplete(block: () -> Unit) {
            this.onComplete = block
        }

        open fun onFailed(block: (code: Int, msg: String, error: Throwable?) -> Unit) {
            this.onFailed = block
        }

    }
}

/**
 * @author : 华清松
 * 常用的 Retrofit 协程请求，方便简单的接口调用，统一的请求状态管理，自行对请求状态进行处理
 */
suspend fun <ResultType> CoroutineScope.retrofit(dsl: RetrofitCoroutineScope.Dsl<ResultType>.() -> Unit) {
    val retrofitCoroutine = RetrofitCoroutineScope.Dsl<ResultType>()
    retrofitCoroutine.dsl()

    try {
        retrofitCoroutine.onStart?.invoke()
        val result = retrofitCoroutine.request.invoke()
        retrofitCoroutine.onSuccess?.invoke(result)
    } catch (e: SocketTimeoutException) {
        retrofitCoroutine.onFailed?.invoke(500, "请求超时", e)
    } catch (e: ConnectException) {
        retrofitCoroutine.onFailed?.invoke(500, "连接失败", e)
    } catch (e: IOException) {
        retrofitCoroutine.onFailed?.invoke(500, "解析失败", e)
    } catch (e: Exception) {
        retrofitCoroutine.onFailed?.invoke(500, "未知错误", e)
    } finally {
        retrofitCoroutine.onComplete?.invoke()
    }
}

/**
 * @author : 华清松
 * 常用的 Retrofit 协程请求，方便简单的接口调用，统一的请求状态管理，自行对请求状态进行处理
 */
suspend fun <ResultType> CoroutineScope.retrofitWithBaseResult(
    dsl: RetrofitCoroutineScope.DslWithBaseResult<IBaseResult<ResultType>>.() -> Unit
) {
    val retrofitCoroutine = RetrofitCoroutineScope.DslWithBaseResult<IBaseResult<ResultType>>()
    retrofitCoroutine.dsl()

    try {
        retrofitCoroutine.onStart?.invoke()
        val result = retrofitCoroutine.request.invoke()
        when {
            result == null -> {
                retrofitCoroutine.onFailed?.invoke(500, "没有回执", null)
            }
            result.ok -> {
                retrofitCoroutine.onSuccess?.invoke(result.data)
            }
            else -> {
                retrofitCoroutine.onFailed?.invoke(result.code, result.msg, null)
            }
        }
    } catch (e: SocketTimeoutException) {
        retrofitCoroutine.onFailed?.invoke(500, "请求超时", e)
    } catch (e: ConnectException) {
        retrofitCoroutine.onFailed?.invoke(500, "连接失败", e)
    } catch (e: IOException) {
        retrofitCoroutine.onFailed?.invoke(500, "解析失败", e)
    } catch (e: Exception) {
        retrofitCoroutine.onFailed?.invoke(500, "未知错误", e)
    } finally {
        retrofitCoroutine.onComplete?.invoke()
    }
}

/**
 * @author : 华清松
 * 常用的 Retrofit 协程请求，方便简单的接口调用，统一的请求状态管理，通过 LiveData 直接更新 UI
 */
suspend fun <ResultType> CoroutineScope.retrofitWithLiveData(
    dsl: RetrofitCoroutineScope.DslWithLiveData<ResultType>.() -> Unit
) {
    val retrofitCoroutine = RetrofitCoroutineScope.DslWithLiveData<ResultType>()
    retrofitCoroutine.dsl()

    try {
        retrofitCoroutine.liveData.httpState.postValue(HttpStatus.start)
        val result = retrofitCoroutine.request.invoke()
        retrofitCoroutine.liveData.postValue(result)
        retrofitCoroutine.liveData.httpState.postValue(HttpStatus.success)
    } catch (e: SocketTimeoutException) {
        retrofitCoroutine.liveData.httpState.postValue(HttpStatus.timeout)
    } catch (e: ConnectException) {
        retrofitCoroutine.liveData.httpState.postValue(HttpStatus.connectError)
    } catch (e: IOException) {
        retrofitCoroutine.liveData.httpState.postValue(HttpStatus.ioError)
    } catch (e: Exception) {
        retrofitCoroutine.liveData.httpState.postValue(HttpStatus.error)
    } finally {
        retrofitCoroutine.liveData.httpState.postValue(HttpStatus.complete)
    }
}


