package org.lulzm.waft.chatModel

class Message {
    // getter & setter
    var message: String? = null
    var type: String? = null
    var time: Long = 0
    var isSeen: Boolean = false
    var from: String? = null
    var send_time: String? = null
    // default constructor
    constructor() {}
    // constructor
    constructor(
        message: String,
        type: String,
        time: Long,
        seen: Boolean,
        from: String,
        send_time: String
    ) {
        this.message = message
        this.type = type
        this.time = time
        this.isSeen = seen
        this.from = from
        this.send_time = send_time
    }
}
