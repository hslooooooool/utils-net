package vip.qsos.core_net.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import vip.qsos.core_net.lib.retrofit.retrofitWithBaseResult
import vip.qsos.core_net.lib.retrofit.retrofitWithLiveData
import vip.qsos.core_net.model.UserInfo
import vip.qsos.core_net.model.UserService

class HomeViewModel : ViewModel() {

    private val mUser = MutableLiveData<UserInfo>()
    private val mUserList = MutableLiveData<List<UserInfo>>().apply {
        value = arrayListOf()
    }

    val user: LiveData<UserInfo>
        get() {
            if (mUser.value == null) {
                loadUser()
            }
            return mUser
        }

    val userList: LiveData<List<UserInfo>>
        get() {
            return mUserList
        }

    private fun loadUser() = viewModelScope.launch {
        retrofitWithLiveData<UserInfo> {
            liveData = mUser
            request { UserService.INSTANCE.getUser().data }
            status {
                Log.d("UserInfo 请求状态", it.toString())
            }
        }
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
            onComplete {
                status.invoke("请求结束")
            }
        }
    }

}