package vip.qsos.core_net.model

import android.content.Context
import vip.qsos.core_net.Application
import vip.qsos.core_net.lib.IAPIServer

class APIServerConfig : IAPIServer {
    override var appContext: Context? = Application.appContext

    override var endPoint: String? = "http://192.168.2.199:8080/"

    override var timeOut: Long? = null

}