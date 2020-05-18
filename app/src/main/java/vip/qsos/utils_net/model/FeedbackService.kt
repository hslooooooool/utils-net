package vip.qsos.utils_net.model

import retrofit2.http.POST
import retrofit2.http.Query
import vip.qsos.utils_net.lib.APIServer

interface FeedbackService {

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

    @POST("/api/feedback")
    suspend fun submit(
        @Query("content") content: String
    ): HttpResult<Boolean>

}