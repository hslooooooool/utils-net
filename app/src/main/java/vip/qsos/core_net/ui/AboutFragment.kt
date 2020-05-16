package vip.qsos.core_net.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_about.view.*
import vip.qsos.core_net.R

class AboutFragment : Fragment() {

    private val mAboutViewModel: AboutViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_about, container, false)
        mAboutViewModel.about.observe(requireActivity(), Observer {
            root.about.text = it ?: "请求错误"
        })
        return root
    }
}