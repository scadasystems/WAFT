package org.lulzm.waft.ChatModel

class Friends {

    var user_name: String? = null
    var user_thumb_image: String? = null
    var date: String? = null

    constructor() {}

    constructor(user_name: String, user_thumb_image: String, date: String) {
        this.user_name = user_name
        this.user_thumb_image = user_thumb_image
        this.date = date
    }
}
