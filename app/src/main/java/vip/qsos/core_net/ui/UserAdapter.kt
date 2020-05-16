package vip.qsos.core_net.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import vip.qsos.core_net.R
import vip.qsos.core_net.model.UserInfo

class UserAdapter : RecyclerView.Adapter<UserAdapter.Holder>() {

    lateinit var data: MutableList<UserInfo>
        private set

    private fun setData(data: MutableList<UserInfo>) {
        this.data = data
    }

    companion object {
        fun create(data: MutableList<UserInfo>): UserAdapter {
            val adapter = UserAdapter()
            adapter.setData(data)
            return adapter
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(data[position], position)
    }

    class Holder(view: View) : RecyclerView.ViewHolder(view) {

        private val vInfo: TextView? by lazy {
            view.findViewById<TextView>(R.id.user_info)
        }

        @SuppressLint("SetTextI18n")
        fun bind(data: UserInfo, position: Int) {
            vInfo?.text = "${position + 1}\n\n" + data.toString()
        }
    }

}