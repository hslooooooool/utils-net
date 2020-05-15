package vip.qsos.core_net.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import vip.qsos.core_net.Application
import vip.qsos.core_net.model.main.UserInfo
import vip.qsos.core_net.model.main.UserService
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException

class MainViewModel : ViewModel() {
    init {
        UserService.appContext = Application.appContext
    }

    private val mUserInfo = MutableLiveData<UserInfo>()

    val userInfo: LiveData<UserInfo>
        get() {
            if (mUserInfo.value == null) {
                loadUserInfo()
            }
            return mUserInfo
        }

    fun loadUserInfo() = viewModelScope.launch {
        try {
            UserService.INSTANCE.getUserInfo()
        } catch (e: SocketTimeoutException) {
            null
        } catch (e: ConnectException) {
            null
        } catch (e: IOException) {
            null
        } catch (e: Exception) {
            null
        }?.let {
            when (it.code) {
                200 -> {
                    mUserInfo.postValue(it.data)
                }
                else -> {

                }
            }
        }
    }
}