package com.di7ak.instalikes

import android.app.Application
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.di7ak.instalikes.net.insta.InstaApi


class App : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        InstaApi.init(baseContext)
        MultiDex.install(this)
    }
}