package vip.qsos.core_net.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import vip.qsos.core_net.lib.expand.retrofit
import vip.qsos.core_net.model.HttpResult
import vip.qsos.core_net.model.main.UserDetailService
import vip.qsos.core_net.model.main.UserInfo

class UserViewModel : ViewModel() {
    private val mUser = MutableLiveData<UserInfo>()

    val user: LiveData<UserInfo>
        get() = mUser

    fun loadUser(toast: (msg: String) -> Unit) = viewModelScope.launch {
        retrofit<HttpResult<UserInfo>> {
            request { UserDetailService.INSTANCE.getUserDetail() }
            onStart {
                toast.invoke("请求开始")
            }
            onSuccess {
                when (it?.code) {
                    200 -> {
                        toast.invoke("请求成功")
                        mUser.postValue(it.data)
                    }
                    else -> {

                    }
                }
            }
            onFailed { code, msg, error ->
                toast.invoke(msg)
            }
            onComplete {
                toast.invoke("请求结束")
            }
        }
    }


}