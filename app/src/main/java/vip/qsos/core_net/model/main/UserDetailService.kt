package vip.qsos.core_net.model.main

import retrofit2.http.GET
import vip.qsos.core_net.lib.APIServer
import vip.qsos.core_net.model.HttpResult
import vip.qsos.core_net.model.mock.UserDetailMockData

interface UserDetailService {

    companion object {
        val INSTANCE: UserDetailService by lazy {
            APIServer.api(
                cls = UserDetailService::class.java,
                config = APIServer.APIConfig(
                    baseUrl = "http://192.168.2.199:8081/",
                    timeout = 3,
                    mockDataList = arrayListOf(
                        UserDetailMockData()
                    )
                )
            )
        }
    }

    @GET("/api/user")
    suspend fun getUserDetail(): HttpResult<UserInfo>

}