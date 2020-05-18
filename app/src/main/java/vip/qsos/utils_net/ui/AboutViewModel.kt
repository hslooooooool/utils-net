package vip.qsos.utils_net.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import vip.qsos.utils_net.lib.callback.HttpLiveData
import vip.qsos.utils_net.lib.retrofit.retrofitWithHttpLiveData
import vip.qsos.utils_net.model.AboutService

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