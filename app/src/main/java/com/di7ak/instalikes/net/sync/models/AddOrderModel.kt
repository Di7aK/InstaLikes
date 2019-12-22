package com.di7ak.instalikes.net.sync.models

import com.google.gson.annotations.SerializedName

data class AddOrderModel(
    var messageText: String = "",
    var messageImg: String = "",
    var status: Boolean = false,
    @SerializedName("error_code") var errorCode: Int = 0
)