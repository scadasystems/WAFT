package org.lulzm.waft.ChatModel

class ProfileInfo {

    var user_name: String? = null
    var verified: String? = null
    var user_image: String? = null
    var user_status: String? = null
    var user_thumb_image: String? = null

    constructor() {}

    constructor(
        user_name: String,
        verified: String,
        user_image: String,
        user_status: String,
        user_thumb_image: String
    ) {
        this.user_name = user_name
        this.verified = verified
        this.user_image = user_image
        this.user_status = user_status
        this.user_thumb_image = user_thumb_image
    }
}
