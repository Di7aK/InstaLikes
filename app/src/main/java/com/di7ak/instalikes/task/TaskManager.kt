package com.di7ak.instalikes.task

import java.util.*

class TaskManager(private val listener: Listener) : Callback {
    private var queue = mutableListOf<TaskItem>()
    private var current: TaskItem? = null
    private var timer: Timer? = null

    fun enqueue(task: Task, delay: Long) {
        queue.add(TaskItem(task, delay))
        if(queue.size == 1) nextTask()
    }

    override fun onSuccess(task: Task) {
        queue.remove(current)
        listener.onTaskComplete(task)
        nextTask()
    }

    override fun onError(task: Task, abort: Boolean, obj: Any?) {
        if(abort) abort()
        else {
            queue.remove(current)
            nextTask()
        }
        listener.onTaskError(task, abort, obj)
    }

    fun abort() {
        timer?.cancel()
        current?.task?.abort()
        queue.clear()
    }

    private fun nextTask() {
        if(queue.isEmpty()) return

        current = queue.first()

        timer = Timer()
        val task = object : TimerTask() {
            override fun run() {
                current?.task?.run {
                    callback = this@TaskManager
                    execute()
                }
            }
        }
        timer?.schedule(task, current!!.delay)
    }

    fun getTaskLeft() = queue.size

    interface Listener {
        fun onTaskComplete(task: Task)
        fun onTaskError(task: Task, abort: Boolean, obj: Any?)
    }

    private data class TaskItem(var task: Task, var delay: Long)
}