package vip.qsos.utils_net.lib.callback

interface IBaseResult<T> {
    val ok: Boolean
    val code: Int
    val msg: String
    val data: T?
}