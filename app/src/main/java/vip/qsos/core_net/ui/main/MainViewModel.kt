package vip.qsos.core_net.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import vip.qsos.core_net.Application
import vip.qsos.core_net.lib.callback.HttpLiveData
import vip.qsos.core_net.lib.expand.retrofitWithBaseResult
import vip.qsos.core_net.lib.expand.retrofitWithLiveData
import vip.qsos.core_net.model.HttpResult
import vip.qsos.core_net.model.main.UserInfo
import vip.qsos.core_net.model.main.UserService

class MainViewModel : ViewModel() {
    init {
        UserService.appContext = Application.appContext
    }

    private val mUserInfo = HttpLiveData<HttpResult<UserInfo>?>()
    private val mUserList = HttpLiveData<List<UserInfo>>()

    val userInfo: HttpLiveData<HttpResult<UserInfo>?>
        get() {
            if (mUserInfo.value == null) {
                loadUserInfo()
            }
            return mUserInfo
        }

    fun loadUserInfo() = viewModelScope.launch {
        retrofitWithLiveData<HttpResult<UserInfo>> {
            liveData = mUserInfo
            request { UserService.INSTANCE.getUserInfo() }
        }
    }

    fun loadUserList() = viewModelScope.launch {
        retrofitWithBaseResult<List<UserInfo>> {
            request { UserService.INSTANCE.getUserList() }
            onSuccess {
                mUserList.postValue(it)
            }
        }
    }

}