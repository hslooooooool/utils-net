package vip.qsos.core_net.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import vip.qsos.core_net.Application
import vip.qsos.core_net.lib.expand.retrofitWithBaseResult
import vip.qsos.core_net.model.UserInfo
import vip.qsos.core_net.model.UserService

class MainViewModel : ViewModel() {
    init {
        UserService.appContext = Application.appContext
    }

    private val mUserList = MutableLiveData<List<UserInfo>>().apply {
        value = arrayListOf()
    }

    val userList: LiveData<List<UserInfo>>
        get() {
            return mUserList
        }

    fun loadList(status: (msg: String) -> Unit) = viewModelScope.launch {
        retrofitWithBaseResult<List<UserInfo>> {
            request { UserService.INSTANCE.getUserList() }
            onStart {
                status.invoke("开始请求")
            }
            onSuccess {
                status.invoke("请求成功")
                mUserList.postValue(it)
            }
            onFailed { code, msg, error ->
                status.invoke(msg)
            }
        }
    }

}