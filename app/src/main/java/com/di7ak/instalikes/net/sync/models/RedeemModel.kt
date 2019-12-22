package com.di7ak.instalikes.net.sync.models

import com.google.gson.annotations.SerializedName

data class RedeemModel(
    var messageText: String = "",
    var messageImg: String = "",
    @SerializedName("error_code") var errorCode: Int = 0
)