package com.di7ak.instalikes.net.sync.models

import com.google.gson.annotations.SerializedName

data class SyncModel(
    var messageText: String = "",
    var messageImg: String = "",
    @SerializedName("user_data") var userData: UserData
) {
    data class UserData(
        var coins: Int = 0
    )
}