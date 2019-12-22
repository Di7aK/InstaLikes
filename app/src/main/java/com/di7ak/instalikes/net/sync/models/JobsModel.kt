package com.di7ak.instalikes.net.sync.models

import com.google.gson.annotations.SerializedName

data class JobsModel(
    var jobs: List<Job>,
    @SerializedName("error_code") var error_code: Int= 0
) {
    data class Job(
        var id: Long = 0,
        @SerializedName("likes_all") var likesAll: Int = 0,
        @SerializedName("likes_remain") var likesRemain: Int = 0,
        var item: JobItem?,
        var user: User?
    )

    data class JobItem(
        @SerializedName("item_id") var itemId: Long = 0,
        @SerializedName("img_src") var imageSrc: String = "",
        var shortcode: String = ""
    )

    data class User(
        var uid: Long = 0,
        var instaname: String = ""
    )
}