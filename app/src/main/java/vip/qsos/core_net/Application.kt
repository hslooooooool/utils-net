package vip.qsos.core_net

import android.app.Application
import android.content.Context
import vip.qsos.core_net.lib.APIServer
import vip.qsos.core_net.lib.mock.IMockData
import vip.qsos.core_net.model.UserService

class Application : Application() {
    companion object {
        lateinit var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this

        UserService.appContext = appContext

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