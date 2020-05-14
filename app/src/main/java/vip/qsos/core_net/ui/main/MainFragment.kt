package vip.qsos.core_net.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.main_fragment.*
import vip.qsos.core_net.R

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.userInfo.observe(requireActivity(), Observer {
            message.text = it.toString()
        })

        message.setOnClickListener {
            startActivity(Intent(requireActivity(), UserActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadUserInfo()
    }
}
