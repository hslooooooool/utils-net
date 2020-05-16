package vip.qsos.core_net.model

import vip.qsos.core_net.lib.mock.AbstractMockData
import vip.qsos.core_net.lib.mock.MockData

class AboutMockData : AbstractMockData() {

    override val config: MockData = MockData(
        "about", "GET", "/api/about", "about.json", 3000
    )

}

class UserListMockData : AbstractMockData() {

    override val config: MockData = MockData(
        "user", "GET", "/api/user/list", "list.json", 3000
    )

}

class FeedbackMockData : AbstractMockData() {

    override val config: MockData = MockData(
        "feedback", "POST", "/api/feedback", "submit.json", 3000
    )

}
