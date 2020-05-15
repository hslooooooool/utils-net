package vip.qsos.core_net.ui.main

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.user_activity.*
import vip.qsos.core_net.R

class UserActivity : AppCompatActivity() {

    private val mUserViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_activity)

        mUserViewModel.user.observe(this, Observer {
            user_info.text = it.toString()
        })

        mUserViewModel.loadUser {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
    }
}