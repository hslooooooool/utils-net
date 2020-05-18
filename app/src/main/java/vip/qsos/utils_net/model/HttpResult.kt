package vip.qsos.utils_net.model

import vip.qsos.utils_net.lib.callback.IBaseResult

data class HttpResult<T>(
    override val code: Int,
    override val msg: String,
    override val data: T?
) : IBaseResult<T> {
    override val ok: Boolean
        get() = code == 200
}