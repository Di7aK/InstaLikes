package com.di7ak.instalikes

import android.app.Application
import com.di7ak.instalikes.net.insta.InstaApi


class App : Application() {

    override fun onCreate() {
        super.onCreate()

        InstaApi.init(baseContext)
    }
}