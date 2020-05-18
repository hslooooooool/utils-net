package vip.qsos.utils_net.ui

import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_submit.*
import vip.qsos.utils_net.R

/**
 * @author : 华清松
 *
 * 提交类请求 DEMO
 */
class SubmitActivity : AppCompatActivity() {

    private val mSubmitViewModel: SubmitViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submit)

        submit.setOnClickListener {
            val content = input.text.toString().trim()
            if (TextUtils.isEmpty(content)) {
                Toast.makeText(this, "反馈内容不能为空", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            submit(content)
        }

    }

    private fun submit(content: String) {
        mSubmitViewModel.submit(
            content,
            status = {
                submit.isEnabled = 2 == it
            },
            result = {
                Toast.makeText(this, if (it) "已提交" else "提交失败", Toast.LENGTH_SHORT).show()
                if (it) {
                    finish()
                }
            }
        )
    }
}