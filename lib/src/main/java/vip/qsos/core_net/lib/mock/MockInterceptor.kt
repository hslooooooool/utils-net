package vip.qsos.core_net.lib.mock

import android.content.Context
import android.os.Environment
import okhttp3.*
import java.io.*
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * @author : 华清松
 *
 * MOCK 请求拦截器
 */
class MockInterceptor constructor(
    private val appContext: Context
) : Interceptor {

    private val mMockDataMap: MutableMap<String, IMockData> = HashMap()

    fun addMockData(mockData: AbstractMockData): MockInterceptor {
        mMockDataMap[mockData.config.key] = mockData
        return this
    }

    fun remove(key: String) {
        mMockDataMap.remove(key)
    }

    fun clear() {
        mMockDataMap.clear()
    }

    fun count(): Int {
        return mMockDataMap.size
    }

    @Throws(IOException::class)
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
        val jsonData = readMockData(mock.path())
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

    private fun readMockData(path: String): String {
        var mockData: String
        val sb = StringBuilder()
        val inputStream: InputStream?
        var reader: BufferedReader? = null
        try {
            inputStream = getMockData(path)
            reader = BufferedReader(InputStreamReader(inputStream))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                sb.append(line)
            }
            mockData = sb.toString()
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
    private fun getMockData(relativePath: String): InputStream {
        val inputStream: InputStream
        inputStream = if (IMockData.dataBySdCard) {
            val file = File(Environment.getExternalStorageState(), relativePath)
            FileInputStream(file)
        } else {
            appContext.assets.open(relativePath)
        }
        return inputStream
    }

    private fun sleep(time: Long) {
        val t = if (IMockData.requestTime > 0) {
            IMockData.requestTime
        } else {
            time
        }
        if (t > 0) {
            TimeUnit.MILLISECONDS.sleep(t)
        }
    }

}