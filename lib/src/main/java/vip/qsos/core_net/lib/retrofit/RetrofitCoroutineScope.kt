package vip.qsos.core_net.lib.retrofit

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import vip.qsos.core_net.lib.callback.HttpLiveData
import vip.qsos.core_net.lib.callback.HttpStatus
import vip.qsos.core_net.lib.callback.IBaseResult
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException

/**Kotlin 协程配置 Retrofit 请求处理逻辑
 * @author : 华清松
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
        lateinit var liveData: (MutableLiveData<ResultType>)
        lateinit var request: suspend () -> ResultType?
        var status: (suspend (status: HttpStatus) -> Unit?)? = null
            private set

        fun request(request: suspend () -> ResultType?) {
            this.request = request
        }

        fun status(status: suspend (status: HttpStatus) -> Unit) {
            this.status = status
        }
    }

    class DslWithHttpLiveData<ResultType> {
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

/**普通请求，自行对请求状态进行处理
 * @author : 华清松
 */
suspend fun <ResultType> CoroutineScope.retrofit(dsl: RetrofitCoroutineScope.Dsl<ResultType>.() -> Unit) {
    val retrofitCoroutine = RetrofitCoroutineScope.Dsl<ResultType>()
    retrofitCoroutine.dsl()

    try {
        retrofitCoroutine.onStart?.invoke()
        val result = retrofitCoroutine.request.invoke()
        retrofitCoroutine.onSuccess?.invoke(result)
    } catch (e: SocketTimeoutException) {
        retrofitCoroutine.onFailed?.invoke(HttpStatus.timeout.code, HttpStatus.timeout.msg, e)
    } catch (e: ConnectException) {
        retrofitCoroutine.onFailed?.invoke(
            HttpStatus.connectError.code, HttpStatus.connectError.msg, e
        )
    } catch (e: IOException) {
        retrofitCoroutine.onFailed?.invoke(HttpStatus.ioError.code, HttpStatus.ioError.msg, e)
    } catch (e: Exception) {
        retrofitCoroutine.onFailed?.invoke(HttpStatus.error.code, HttpStatus.error.msg, e)
    } finally {
        retrofitCoroutine.onComplete?.invoke()
    }
}

/**采用默认的返回对象，返回对象需实现 IBaseResult 接口
 * @author : 华清松
 * @see IBaseResult
 */
suspend fun <ResultType> CoroutineScope.retrofitWithBaseResult(
    dsl: RetrofitCoroutineScope.DslWithBaseResult<ResultType>.() -> Unit
) {
    val retrofitCoroutine = RetrofitCoroutineScope.DslWithBaseResult<ResultType>()
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
        retrofitCoroutine.onFailed?.invoke(HttpStatus.timeout.code, HttpStatus.timeout.msg, e)
    } catch (e: ConnectException) {
        retrofitCoroutine.onFailed?.invoke(
            HttpStatus.connectError.code, HttpStatus.connectError.msg, e
        )
    } catch (e: IOException) {
        retrofitCoroutine.onFailed?.invoke(HttpStatus.ioError.code, HttpStatus.ioError.msg, e)
    } catch (e: Exception) {
        retrofitCoroutine.onFailed?.invoke(HttpStatus.error.code, HttpStatus.error.msg, e)
    } finally {
        retrofitCoroutine.onComplete?.invoke()
    }
}

/**通过 MutableLiveData 更新 UI，通过 status 方法处理请求状态
 * @author : 华清松
 * @see MutableLiveData
 */
suspend fun <ResultType> CoroutineScope.retrofitWithLiveData(
    dsl: RetrofitCoroutineScope.DslWithLiveData<ResultType>.() -> Unit
) {
    val retrofitCoroutine = RetrofitCoroutineScope.DslWithLiveData<ResultType>()
    retrofitCoroutine.dsl()

    try {
        retrofitCoroutine.status?.invoke(HttpStatus.start)
        val result = retrofitCoroutine.request.invoke()
        retrofitCoroutine.liveData.postValue(result)
        retrofitCoroutine.status?.invoke(HttpStatus.success)
    } catch (e: SocketTimeoutException) {
        retrofitCoroutine.status?.invoke(HttpStatus.timeout)
    } catch (e: ConnectException) {
        retrofitCoroutine.status?.invoke(HttpStatus.connectError)
    } catch (e: IOException) {
        retrofitCoroutine.status?.invoke(HttpStatus.ioError)
    } catch (e: Exception) {
        retrofitCoroutine.status?.invoke(HttpStatus.error)
    } finally {
        retrofitCoroutine.status?.invoke(HttpStatus.complete)
    }
}

/**采用默认的带状态监控的 HttpLiveData 更新 UI，HttpLiveData 内包含一个观察请求状态的 LiveData 。
 * @author : 华清松
 * @see HttpLiveData
 */
suspend fun <ResultType> CoroutineScope.retrofitWithHttpLiveData(
    dsl: RetrofitCoroutineScope.DslWithHttpLiveData<ResultType>.() -> Unit
) {
    val retrofitCoroutine = RetrofitCoroutineScope.DslWithHttpLiveData<ResultType>()
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