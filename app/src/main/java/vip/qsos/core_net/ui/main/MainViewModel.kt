package vip.qsos.core_net.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import vip.qsos.core_net.Application
import vip.qsos.core_net.model.main.UserInfo
import vip.qsos.core_net.model.main.UserService

class MainViewModel : ViewModel() {

    private val mUserInfo = MutableLiveData<UserInfo>()

    val userInfo: LiveData<UserInfo>
        get() {
            if (mUserInfo.value == null) {
                loadUserInfo()
            }
            return mUserInfo
        }

    fun loadUserInfo() = viewModelScope.launch {
        UserService.appContext = Application.appContext
        val user = UserService.INSTANCE.getUserInfo()
        when (user.code) {
            200 -> {
                mUserInfo.postValue(user.data)
            }
            else -> {

            }
        }
    }
}