package com.magicalrice.project.library_data.bean

data class NetErrorBean(
    var timestamp: Long,
    var status: Int,
    var error: String,
    var message: String,
    var path: String
)