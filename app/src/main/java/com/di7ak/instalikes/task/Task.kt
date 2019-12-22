package com.di7ak.instalikes.task

abstract class Task {
    var callback: Callback? = null

    abstract fun execute()

    abstract fun abort()

    fun success() {
        callback?.onSuccess(this)
    }

    fun error(abort: Boolean = false, obj: Any? = null) {
        callback?.onError(this, abort, obj)
    }
}