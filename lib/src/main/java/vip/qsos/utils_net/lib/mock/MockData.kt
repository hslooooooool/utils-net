package vip.qsos.utils_net.lib.mock

import java.util.*

/**MOCK 配置数据
 *
 * @author : 华清松
 * @param group Mock 数据文件组名
 * @param method 请求方式。GET POST PUT DELETE 等
 * @param path 请求路径
 * @param filename Mock 数据文件名称
 * @param requestTime 请求时长。毫秒
 */
data class MockData(
    val group: String,
    val method: String,
    val path: String,
    val filename: String,
    val requestTime: Long = 0L
) {
    /**Mock数据配置识别 Key，由 method + path 组成*/
    val key = method.toUpperCase(Locale.ENGLISH) + path
}

/**Mock 数据配置与接口
 *
 * @author : 华清松
 * */
interface IMockData {

    companion object {
        /**是否开启网络请求读取Mock数据
         *
         * 默认 false
         * */
        var openMockData: Boolean = false

        /**Mock数据是否从SD卡读取
         *
         * 默认 false 从包资源 assets 中读取
         * */
        var dataBySdCard: Boolean = false

        /**Mock数据网络请求时长
         *
         * 默认 0 毫秒，此时请求的超时时间将交由每个 MockData 自行控制，超过 0L 时，则全部 Mock 请求采用此超时时长
         * @see IMockData.requestTime
         * */
        var requestTime: Long = 0L
    }

    /**MOCK 配置数据*/
    val config: MockData

    /**Mock 数据文件路径
     *
     * 默认 mock/$group/ 组成
     * @see AbstractMockData.path
     * */
    fun path(): String

    /**Mock 开关
     *
     * 默认由 IMockData.openMockData 决定
     * @see IMockData.openMockData
     * */
    fun mock(): Boolean
}

/**Mock 接口默认实现
 *
 * @author : 华清松
 * */
abstract class AbstractMockData : IMockData {

    /**Mock 数据文件路径默认组成方式*/
    override fun path(): String {
        return "mock/${config.group}/"
    }

    /**Mock 开关默认状态*/
    override fun mock(): Boolean {
        return IMockData.openMockData
    }
}