package com.di7ak.instalikes.net.insta.models

import com.google.gson.annotations.SerializedName

data class PostModel(
    var status: String,
    var message: String = "",
    var graphql: Graphql
) {
    data class Graphql(
        @SerializedName("shortcode_media") var shortcodeMedia: ShortcodeMedia
    )

    data class ShortcodeMedia(
        @SerializedName("display_url") var displayUrl: String
    )
}