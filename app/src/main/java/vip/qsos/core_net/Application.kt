package vip.qsos.core_net

import android.app.Application
import android.content.Context
import vip.qsos.core_net.lib.APIServer
import vip.qsos.core_net.lib.mock.IMockData
import vip.qsos.core_net.model.APIServerConfig

class Application : Application() {
    companion object {
        lateinit var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this

        // 开启网络请求Mock数据
        IMockData.openMockData = true
        IMockData.dataBySdCard = false
        IMockData.requestTime = 0

        APIServer.init(APIServerConfig())
    }
}