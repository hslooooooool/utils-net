# Retrofit Kotlin 协程网络请求封装工具类

提供基本 Retrofit 使用，借助 kotlin dsl 实现请求配置，无特定需求可直接使用以下方法开始请求。核心类见：[RetrofitCoroutineScope](/lib/src/main/java/vip/qsos/core_net/lib/retrofit/RetrofitCoroutineScope.kt)

- retrofit
普通请求，自行对请求状态进行处理。
例如：[SubmitViewModel.submit](/app/src/main/java/vip/qsos/core_net/ui/SubmitViewModel.kt)
- retrofitWithBaseResult
采用默认的返回对象，返回对象需实现 [IBaseResult](/lib/src/main/java/vip/qsos/core_net/lib/callback/IBaseResult.kt) 接口。
例如：[HomeViewModel.loadList](/app/src/main/java/vip/qsos/core_net/ui/HomeViewModel.kt)
- retrofitWithLiveData
通通过 MutableLiveData 更新 UI，通过 status 方法处理请求状态。
例如：[HomeViewModel.loadUser](/app/src/main/java/vip/qsos/core_net/ui/HomeViewModel.kt)
- retrofitWithHttpLiveData
采用默认的带状态监控的 HttpLiveData 更新 UI，[HttpLiveData](/lib/src/main/java/vip/qsos/core_net/lib/callback/HttpLiveData.kt) 内包含一个观察请求状态的 LiveData 。
例如：[HttpResult.loadAbout](/app/src/main/java/vip/qsos/core_net/ui/AboutViewModel.kt)

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

## retrofit<>{}
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

## retrofitWithBaseResult<>{}
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

## retrofitWithLiveData<>{}
```kotlin
        retrofitWithLiveData<Result> {
            liveData = mData
            request { Service.INSTANCE.get() }
            status {
                // 请求状态执行
            }
        }
```

## retrofitWithHttpLiveData<>{}
```kotlin
        retrofitWithHttpLiveData<Result?> {
            liveData = mData
            request { Service.INSTANCE.get().data }
        }
```

