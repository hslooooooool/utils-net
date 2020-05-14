package vip.qsos.core_net.model.main

data class UserInfo(
        val name: String,
        val sexy: String,
        val age: Int
) {
    override fun toString(): String {
        return "name=$name,sexy=$sexy,age=$age"
    }
}