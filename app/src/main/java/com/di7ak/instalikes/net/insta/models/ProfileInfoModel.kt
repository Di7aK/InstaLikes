package com.di7ak.instalikes.net.insta.models

import com.google.gson.annotations.SerializedName

data class ProfileInfoModel(
    var graphql: Graphql?
) {

    data class Data(
        var data: Graphql
    )

    data class Graphql(
        var user: User?
    )

    data class User(
        @SerializedName("full_name") var fullName: String = "",
        var id: Long = 0,
        @SerializedName("is_private") var isPrivate: Boolean = false,
        @SerializedName("profile_pic_url") var profilePic: String = "",
        @SerializedName("edge_owner_to_timeline_media") var timelineMedia: TimelineMedia
    )

    data class TimelineMedia(
        var count: Int = 0,
        @SerializedName("page_info") var pageInfo: PageInfo,
        var edges: List<Edges>
    )

    data class PageInfo(
        @SerializedName("has_next_page") var hasNextPage: Boolean = false,
        @SerializedName("end_cursor") var endCursor: String = ""
    )

    data class Edges(
        var node: Node
    )

    data class Node(
        var id: Long = 0,
        var shortcode: String = "",
        @SerializedName("display_url") var displayUrl: String = "",
        @SerializedName("edge_media_to_caption") var mediaCaption: MediaCaption
    ) {
        data class MediaCaption(
            var edges: List<Edges>
        )
        data class Edges(
            var node: Node
        )

        data class Node(
            var text: String = ""
        )
    }
}