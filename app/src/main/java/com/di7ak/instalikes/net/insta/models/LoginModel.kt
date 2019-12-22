package com.di7ak.instalikes.net.insta.models

import com.google.gson.annotations.SerializedName

data class LoginModel(
    var status: String,
    var message: String = "",
    var authenticated: Boolean = false,
    var userId: Long = 0L,

    @SerializedName("checkpoint_url") var checkpointUrl: String = "",
    @SerializedName("two_factor_required") var twoFactorRequired: Boolean = false,
    @SerializedName("two_factor_info") var twoFactorInfo: TwoFactorInfo? = null
) {
    data class TwoFactorInfo(
        @SerializedName("two_factor_identifier") var identifier: String = "",
        var username: String = ""
    )
}