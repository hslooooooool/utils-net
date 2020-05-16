package vip.qsos.core_net.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import vip.qsos.core_net.lib.expand.retrofit
import vip.qsos.core_net.model.HttpResult
import vip.qsos.core_net.model.AboutService

class AboutViewModel : ViewModel() {

    private val mAbout = MutableLiveData<String>()

    val about: LiveData<String>
        get() {
            if (mAbout.value == null) {
                loadAbout()
            }
            return mAbout
        }

    private fun loadAbout() = viewModelScope.launch {
        retrofit<HttpResult<String>> {
            request { AboutService.INSTANCE.about() }
            onSuccess {
                when (it?.code) {
                    200 -> {
                        mAbout.postValue(it.data)
                    }
                    else -> {

                    }
                }
            }
        }
    }


}