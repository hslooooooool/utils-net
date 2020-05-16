package vip.qsos.core_net.model

import retrofit2.http.GET
import vip.qsos.core_net.lib.APIServer

interface AboutService {

    companion object {
        val INSTANCE: AboutService by lazy {
            APIServer.api(
                cls = AboutService::class.java,
                config = APIServer.APIConfig(
                    baseUrl = "http://192.168.2.199:8081/",
                    timeout = 3,
                    mockDataList = arrayListOf(
                        AboutMockData()
                    )
                )
            )
        }
    }

    @GET("/api/about")
    suspend fun about(): HttpResult<String>

}