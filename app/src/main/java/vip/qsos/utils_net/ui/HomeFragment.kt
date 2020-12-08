package vip.qsos.utils_net.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_home.view.*
import vip.qsos.utils_net.R

class HomeFragment : Fragment() {

    /*注意此处获取的是对应Activity的ViewModel*/
    private val viewModel: HomeViewModel by activityViewModels()
    private lateinit var mAdapter: UserAdapter
    private lateinit var vUserList: RecyclerView
    private lateinit var vStatus: TextView
    private lateinit var vSubmit: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        mAdapter = UserAdapter.create(arrayListOf())
        vStatus = root.status
        vSubmit = root.submit
        vUserList = root.user_list
        vUserList.adapter = mAdapter
        vUserList.layoutManager = GridLayoutManager(requireContext(), 2)

        vSubmit.setOnClickListener {
            startActivity(Intent(requireActivity(), SubmitActivity::class.java))
        }

        viewModel.userList.observe(requireActivity(), Observer {
            mAdapter.data.addAll(it)
            mAdapter.notifyDataSetChanged()
        })

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.loadList {
            vStatus.text = it
        }
    }
}
