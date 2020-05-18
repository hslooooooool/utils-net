package vip.qsos.utils_net.lib.mock

import android.content.Context
import okhttp3.*
import retrofit2.HttpException
import java.io.*
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * @author : 华清松
 *
 * MOCK请求拦截器
 */
class MockInterceptor constructor(
    private val appContext: Context
) : Interceptor {

    private val mMockDataMap: MutableMap<String, IMockData> = HashMap()

    /**添加 MOCK 数据配置*/
    fun addMockData(mockData: AbstractMockData): MockInterceptor {
        mMockDataMap[mockData.config.key] = mockData
        return this
    }

    /**移除 KEY 对应的 MOCK 数据配置*/
    fun remove(key: String) {
        mMockDataMap.remove(key)
    }

    /**清除 MOCK 数据配置*/
    fun clear() {
        mMockDataMap.clear()
    }

    /**获取 MOCK 数据配置数量*/
    fun count(): Int {
        return mMockDataMap.size
    }

    @Throws(HttpException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val key = request.method() + request.url().url().path
        val mock = mMockDataMap[key]
        return if (IMockData.openMockData && mock != null && mock.mock()) {
            requestMockData(request, mock)
        } else {
            chain.proceed(request)
        }
    }

    private fun requestMockData(request: Request, mock: IMockData): Response {
        sleep(mock.config.requestTime)
        val jsonData = readMockData(mock.path(), mock.config.filename)
        return Response.Builder()
            .code(200)
            .message(jsonData)
            .request(request)
            .protocol(Protocol.HTTP_1_0)
            .body(
                ResponseBody.create(
                    MediaType.parse("application/json"),
                    jsonData.toByteArray(charset("UTF-8"))
                )
            )
            .addHeader("content-type", "application/json")
            .build()
    }

    private fun readMockData(path: String, filename: String): String {
        var mockData: String
        val sb = StringBuilder()
        val inputStream: InputStream?
        var reader: BufferedReader? = null
        try {
            inputStream = getMockData(path, filename)
            if (inputStream == null) {
                mockData = ""
            } else {
                reader = BufferedReader(InputStreamReader(inputStream))
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    sb.append(line)
                }
                mockData = sb.toString()
            }
        } catch (e: Exception) {
            mockData = ""
        } finally {
            try {
                reader?.close()
            } catch (e: Exception) {
                mockData = ""
            }
        }
        return mockData
    }

    @Throws(IOException::class)
    private fun getMockData(path: String, filename: String): InputStream? {
        val inputStream: InputStream?
        inputStream = if (IMockData.dataBySdCard) {
            // /data/user/0/package-name/files 随卸载删除
            val f1 = appContext.getExternalFilesDir(null)
            f1?.mkdirs()
            val f2 = File(f1, path)
            f2.mkdirs()
            val f3 = File(f2, filename)
            if (!f3.exists()) {
                f3.createNewFile()
            }
            FileInputStream(f3)
        } else {
            try {
                appContext.assets.open(path + filename)
            } catch (e: Exception) {
                null
            }
        }
        return inputStream
    }

    private fun sleep(time: Long) {
        val t = if (time > 0L) {
            time
        } else {
            IMockData.requestTime
        }
        if (t > 0L) {
            TimeUnit.MILLISECONDS.sleep(t)
        }
    }

}