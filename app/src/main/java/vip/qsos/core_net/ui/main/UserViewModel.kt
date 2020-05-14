package vip.qsos.core_net.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import vip.qsos.core_net.model.main.UserDetailService
import vip.qsos.core_net.model.main.UserInfo

class UserViewModel : ViewModel() {
    private val mUser = MutableLiveData<UserInfo>()

    val user: LiveData<UserInfo>
        get() = mUser

    fun loadUser() = viewModelScope.launch {
        val user = UserDetailService.INSTANCE.getUserDetail()
        when (user.code) {
            200 -> {
                mUser.postValue(user.data)
            }
        }
    }
}