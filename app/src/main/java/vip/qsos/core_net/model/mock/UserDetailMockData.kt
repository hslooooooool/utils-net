package vip.qsos.core_net.model.mock

import vip.qsos.core_net.lib.mock.AbstractMockData
import vip.qsos.core_net.lib.mock.MockData

class UserDetailMockData : AbstractMockData() {

    override val config: MockData = MockData(
        "account", "GET", "/api/user", "user-info.json", 5000
    )

}
