package vip.qsos.core_net.model.mock

import vip.qsos.core_net.lib.mock.AbstractMockData
import vip.qsos.core_net.lib.mock.MockData

class UserMockData : AbstractMockData() {

    override val config: MockData = MockData(
        "account", "GET", "/api/user", "user.json", 5000
    )

}
