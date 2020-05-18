package vip.qsos.utils_net.model

data class UserInfo(
    val name: String,
    val sexy: String,
    val age: Int
) {
    override fun toString(): String {
        return "姓名：$name \n性别：$sexy \n年龄：$age"
    }
}