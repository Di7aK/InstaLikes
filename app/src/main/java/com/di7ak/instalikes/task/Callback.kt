package com.di7ak.instalikes.task

interface Callback {
    fun onSuccess(task: Task)

    fun onError(task: Task, abort: Boolean, obj: Any?)
}