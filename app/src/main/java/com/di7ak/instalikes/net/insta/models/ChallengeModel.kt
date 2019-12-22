package com.di7ak.instalikes.net.insta.models

data class ChallengeModel(
    var status: String,
    var message: String? = "",
    var challengeType: String = "",
    var navigation: Navigation?
) {
    data class Navigation(
        var forward: String = "",
        var replay: String = ""
    )
}