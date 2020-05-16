package vip.qsos.core_net.lib.callback

import androidx.lifecycle.MutableLiveData

/**
 * @author : 华清松
 * 如果数据请求存在多种状态，而这些状态需要被观察，则使用此类代替，
 * 观察的数据将默认持有一个可观测请求状态的 MutableLiveData 即 httpState
 */
class HttpLiveData<T> : MutableLiveData<T>() {
    /**网络请求状态*/
    val httpState = MutableLiveData<HttpStatus>()
}