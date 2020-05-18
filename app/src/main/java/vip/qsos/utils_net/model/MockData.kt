package vip.qsos.utils_net.model

import vip.qsos.utils_net.lib.mock.AbstractMockData
import vip.qsos.utils_net.lib.mock.MockData

class AboutMockData : AbstractMockData() {

    override val config: MockData = MockData(
        "about", "GET", "/api/about", "about.json", 100L
    )

}

class UserMockData : AbstractMockData() {

    override val config: MockData = MockData(
        "user", "GET", "/api/user", "user.json", 1000L
    )

}

class UserListMockData : AbstractMockData() {

    override val config: MockData = MockData(
        "user", "GET", "/api/user/list", "list.json", 2000L
    )

}

class FeedbackMockData : AbstractMockData() {

    override val config: MockData = MockData(
        "feedback", "POST", "/api/feedback", "submit.json", 2000L
    )

}
