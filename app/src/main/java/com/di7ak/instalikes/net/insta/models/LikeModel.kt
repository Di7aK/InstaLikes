package com.di7ak.instalikes.net.insta.models

import com.google.gson.annotations.SerializedName

data class LikeModel(
    var status: String,
    var message: String = "",
    var spam: Boolean = false,
    @SerializedName("feedback_url") var feedbackUrl: String = ""
)