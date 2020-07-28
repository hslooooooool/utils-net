package vip.qsos.utils_net.lib.callback

/**网络请求状态参数
 * @param msg 结果信息
 * @param error 异常
 * @param code 请求结果码。默认设置几个，如下：
 *
 * -3, "解析错误"
 *
 * -2, "请求超时"
 *
 * -1, "连接失败"
 *
 * 0, "请求开始"
 *
 * 1, "请求结束"
 *
 * 200, "请求成功"
 *
 * 500, "请求错误"
 *
 * @see HttpStatus.start
 * @see HttpStatus.ioError
 * @see HttpStatus.connectError
 * @see HttpStatus.timeout
 * @see HttpStatus.error
 * @see HttpStatus.success
 * @see HttpStatus.complete
 */
data class HttpStatus(
    val code: Int = 200,
    val msg: String = "请求成功",
    val error: Throwable? = null
) {
    companion object {

        val ioError: HttpStatus by lazy {
            HttpStatus(-3, "解析错误")
        }

        val timeout: HttpStatus by lazy {
            HttpStatus(-2, "请求超时")
        }

        val connectError: HttpStatus by lazy {
            HttpStatus(-1, "连接失败")
        }

        val start: HttpStatus by lazy {
            HttpStatus(0, "请求开始")
        }

        val complete: HttpStatus by lazy {
            HttpStatus(1, "请求结束")
        }

        val success: HttpStatus by lazy {
            HttpStatus(200, "请求成功")
        }

        val error: HttpStatus by lazy {
            HttpStatus(500, "请求错误")
        }

    }

    override fun toString(): String {
        return "code=$code ,msg=$msg ,error=${error?.message}"
    }
}
