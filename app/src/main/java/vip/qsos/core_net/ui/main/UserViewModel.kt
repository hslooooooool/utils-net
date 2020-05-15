package vip.qsos.core_net.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonIOException
import kotlinx.coroutines.launch
import vip.qsos.core_net.model.main.UserDetailService
import vip.qsos.core_net.model.main.UserInfo
import java.io.IOException
import java.net.ConnectException

class UserViewModel : ViewModel() {
    private val mUser = MutableLiveData<UserInfo>()

    val user: LiveData<UserInfo>
        get() = mUser

    fun loadUser() = viewModelScope.launch {
        try {
            UserDetailService.INSTANCE.getUserDetail()
        } catch (e: ConnectException) {
            null
        } catch (e: IOException) {
            null
        } catch (e: Exception) {
            null
        }?.let {
            when (it.code) {
                200 -> {
                    mUser.postValue(it.data)
                }
            }
        }
    }
}