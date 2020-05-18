# Retrofit Kotlin 协程网络请求封装

直接使用，引入依赖:
```groovy
    implementation 'vip.qsos.utils-net:utils-net:1.0'
```

## 1. 提供基本 Retrofit 使用

借助 kotlin dsl 实现请求配置，无特定需求可直接使用以下方法开始请求。核心类见：[RetrofitCoroutineScope](/lib/src/main/java/vip/qsos/core_net/lib/retrofit/RetrofitCoroutineScope.kt)
请求将带有 suspend 标识，默认在 ViewModel 中进行请求，使用 ViewModel 自行管理请求生命周期，如 [AboutViewModel](/app/src/main/java/vip/qsos/utils_net/ui/AboutViewModel.kt) ：
```kotlin
class AboutFragment : Fragment() {

    private val mAboutViewModel: AboutViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_about, container, false)
        mAboutViewModel.about.observe(requireActivity(), Observer {
            root.about.text = it ?: "请求错误"
        })
        return root
    }
}
class AboutViewModel : ViewModel() {

    private val mAbout = HttpLiveData<String?>()

    val about: HttpLiveData<String?>
        get() {
            if (mAbout.value == null) {
                loadAbout()
            }
            return mAbout
        }

    private fun loadAbout() = viewModelScope.launch {
        retrofitWithHttpLiveData<String> {
            liveData = mAbout
            request { AboutService.INSTANCE.about().data }
        }
    }
    
}
```

默认拓展了以下几种请求方式：

- retrofit
普通请求，自行对请求状态进行处理。
例如：[SubmitViewModel.submit](/app/src/main/java/vip/qsos/utils_net/ui/SubmitViewModel.kt)
- retrofitWithBaseResult
采用默认的返回对象，返回对象需实现 [IBaseResult](/lib/src/main/java/vip/qsos/core_net/lib/callback/IBaseResult.kt) 接口。
例如：[HomeViewModel.loadList](/app/src/main/java/vip/qsos/utils_net/ui/HomeViewModel.kt)
- retrofitWithLiveData
通通过 MutableLiveData 更新 UI，通过 status 方法处理请求状态。
例如：[HomeViewModel.loadUser](/app/src/main/java/vip/qsos/utils_net/ui/HomeViewModel.kt)
- retrofitWithHttpLiveData
采用默认的带状态监控的 HttpLiveData 更新 UI，[HttpLiveData](/lib/src/main/java/vip/qsos/core_net/lib/callback/HttpLiveData.kt) 内包含一个观察请求状态的 LiveData 。
例如：[HttpResult.loadAbout](/app/src/main/java/vip/qsos/utils_net/ui/AboutViewModel.kt)

以上带请求状态 [HttpStatus](/lib/src/main/java/vip/qsos/core_net/lib/callback/HttpStatus.kt) 数据的请求默认响应了以下几种状态：
```kotlin
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
```

下面列举上述请求示例代码。

### retrofit<>{}
```kotlin
        retrofit<BaseResult<Result>> {
            request { Service.INSTANCE.get() }
            onStart {
                // 请求开始执行
            }
            onSuccess { result ->
                // 请求成功执行，返回result
            }
            onFailed { code, msg, error ->
                // 请求失败执行，返回错误码=code ，错误信息=msg ，以及Throwable=error
            }
            onComplete {
                // 请求结束执行
            }
        }
```

### retrofitWithBaseResult<>{}
```kotlin
        retrofitWithBaseResult<Result> {
            request { Service.INSTANCE.get() }
            onStart {
                // 请求开始执行
            }
            onSuccess { result ->
                // 请求成功执行，返回result
            }
            onFailed { code, msg, error ->
                // 请求失败执行，返回错误码=code ，错误信息=msg ，以及Throwable=error
            }
            onComplete {
                // 请求结束执行
            }
        }
```

### retrofitWithLiveData<>{}
```kotlin
        retrofitWithLiveData<Result> {
            liveData = mData
            request { Service.INSTANCE.get() }
            status {
                // 请求状态执行
            }
        }
```

### retrofitWithHttpLiveData<>{}
```kotlin
        retrofitWithHttpLiveData<Result> {
            liveData = mData
            request { Service.INSTANCE.get() }
        }
```

## 2.提供网络请求MOCK数据

采用加载本地assets/mock.json资源文件或sd卡下的mock.json资源文件，返回给网络请求mock数据，mock数据的替换使用网络拦截器实现，核心类见：[MockInterceptor](lib/src/main/java/vip/qsos/utils_net/lib/mock/MockInterceptor.kt) 。
使用时，首先定义接口返回的mock数据参数，通过实现 [IMockData](/lib/src/main/java/vip/qsos/utils_net/lib/mock/MockData.kt) 接口创建Mock配置，
或继承默认配置 [AbstractMockData](/lib/src/main/java/vip/qsos/utils_net/lib/mock/MockData.kt) 实现，例如：
```kotlin
class UserMockData : AbstractMockData() {

    override val config: MockData = MockData(
        "user", "GET", "/api/user", "user.json", 3000L
    )

}
```
其中 [MockData](/lib/src/main/java/vip/qsos/utils_net/lib/mock/MockData.kt) 为此接口的具体配置，包括：
- 接口组名
用于分组，在默认配置中，表示为Mock文件的存放文件夹名称
- 请求方式
用于代表POST、GET、PUT、DELETE等请求方式
- 接口路径
用于确定接口路径，结合请求方式在拦截器中作为接口识别Mock数据的Key
- Mock文件名
用于确认Mock文件名称，在默认配置中，结合接口组名确定Mock文件的具体路径
- 请求时长
用于模拟请求耗时操作，为了留给请求一定时间，实现UI交互动效

在默认配置 [AbstractMockData](/lib/src/main/java/vip/qsos/utils_net/lib/mock/MockData.kt) 中，默认约定了Mock数据文件的路径和是否开启此接口的Mock配置的开关条件，可根据需要重写，代码如下：
```kotlin
abstract class AbstractMockData : IMockData {

    /**Mock 数据文件路径默认组成方式*/
    override fun path(): String {
        return "mock/${config.group}/"
    }

    /**Mock 开关默认状态*/
    override fun mock(): Boolean {
        return BuildConfig.DEBUG
    }
}
```
在使用 MockData 时，你可自行构建 Retrofit 请求，如 [UserService](\app\src\main\java\vip\qsos\core_net\model\UserService.kt)：
```kotlin
    companion object {

        private const val BASE_URL = "http://192.168.2.199:8080/"

        var appContext: Context? = null
        var timeout: Long = 8000L

        val INSTANCE: UserService by lazy {
            val mClient = OkHttpClient.Builder()
            mClient.connectTimeout(timeout, TimeUnit.MILLISECONDS)
            appContext?.let {
                val interceptor = MockInterceptor(it)
                interceptor.addMockData(UserMockData())
                interceptor.addMockData(UserListMockData())
                mClient.addInterceptor(interceptor)
            }
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .callFactory(mClient.build())
                .addConverterFactory(GsonConverterFactory.create(Gson()))
                .build()
                .create(UserService::class.java)
        }

    }

```
或，使用快捷方式构建，如 [FeedbackService](\app\src\main\java\vip\qsos\core_net\model\FeedbackService.kt)：
```kotlin
    companion object {
        val INSTANCE: FeedbackService by lazy {
            APIServer.api(
                cls = FeedbackService::class.java,
                config = APIServer.APIConfig(
                    baseUrl = "http://192.168.2.199:8080/",
                    timeout = 3000L,
                    mockDataList = arrayListOf(
                        FeedbackMockData()
                    )
                )
            )
        }
    }
```
以上 config 为可选配置，配置后将使用通用配置，否则使用 MockData 内的配置，如无需 Mock 数据服务，则可不配此配置，如：
```kotlin
    companion object {
        val INSTANCE: FeedbackService by lazy {
            APIServer.api(FeedbackService::class.java)
        }
    }
```

最后，你可能需要在使用网络请求前，进行初始化，详见：[Application](\app\src\main\java\vip\qsos\core_net\Application.kt) , [MockData](/lib/src/main/java/vip/qsos/utils_net/lib/mock/MockData.kt) , [APIServer](\lib\src\main\java\vip\qsos\core_net\lib\APIServer.kt) ：
```kotlin
class Application : Application() {

    override fun onCreate() {
        super.onCreate()

        // 开启网络请求Mock数据
        IMockData.openMockData = true
        IMockData.dataBySdCard = false
        IMockData.requestTime = 0L

        // 初始化网络请求
        APIServer.init(
            APIServer.Config(this, "http://192.168.2.199:8080/")
        )
    }
}
```