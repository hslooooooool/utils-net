package vip.qsos.core_net.model

data class HttpResult<T>(
    val code: Int,
    val msg: String,
    val data: T?
)