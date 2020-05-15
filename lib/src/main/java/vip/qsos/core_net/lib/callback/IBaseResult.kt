package vip.qsos.core_net.lib.callback

interface IBaseResult<T> {
    val ok: Boolean
    val code: Int
    val msg: String
    val data: T?
}