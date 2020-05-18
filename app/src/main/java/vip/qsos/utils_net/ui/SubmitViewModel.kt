package vip.qsos.utils_net.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import vip.qsos.utils_net.lib.retrofit.retrofit
import vip.qsos.utils_net.model.HttpResult
import vip.qsos.utils_net.model.FeedbackService

class SubmitViewModel : ViewModel() {

    fun submit(
        content: String,
        status: (status: Int) -> Unit,
        result: (result: Boolean) -> Unit
    ) = viewModelScope.launch {
        retrofit<HttpResult<Boolean>> {
            request { FeedbackService.INSTANCE.submit(content) }
            onStart {
                status.invoke(1)
            }
            onSuccess {
                result.invoke(true == it?.data)
            }
            onFailed { code, _, error ->
                error?.printStackTrace()
                status.invoke(code)
                result.invoke(false)
            }
            onComplete {
                status.invoke(2)
            }
        }
    }

}