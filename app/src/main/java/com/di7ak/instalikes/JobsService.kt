package com.di7ak.instalikes

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.di7ak.instalikes.net.ApiRequest
import com.di7ak.instalikes.net.insta.InstaApi
import com.di7ak.instalikes.net.insta.models.LikeModel
import com.di7ak.instalikes.net.insta.models.ProfileInfoModel
import com.di7ak.instalikes.net.server.ServerApi
import com.di7ak.instalikes.net.sync.SyncApi
import com.di7ak.instalikes.net.sync.models.JobsModel
import com.di7ak.instalikes.task.Task
import com.di7ak.instalikes.task.TaskManager
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import java.util.*

class JobsService : Service(), TaskManager.Listener {
    companion object {
        private const val ACTION_START_JOBS = "start_jobs"
        private const val ACTION_STOP_JOBS = "stop_jobs"

        private const val PREFERENCE_NAME = "jobs_preferences"
        private const val PREFERENCE_KEY_LAST_ORDER_TIME = "last_order_created"
        private const val PREFERENCE_KEY_LAST_LIKE_TIME = "last_like"

        private const val LIKE_MIN_INTERVAL = 2//sec
        private const val LIKE_MAX_INTERVAL = 3//sec
        private const val ORDER_INTERVAL = 1000 * 60 * 30L
        private const val LIKE_INTERVAL = 1000 * 60 * 30L

        private const val MEDIA_LIMIT = 50
        private const val ORDER_LIMIT = 10
        private const val ORDER_PRICE = 20

        private const val TAG = "#instakelli"
        var num = 0

        fun start(context: Context) {
            context.startService(Intent(context, JobsService::class.java).apply {
                action = ACTION_START_JOBS
            })
        }

        fun stop(context: Context) {
            context.startService(Intent(context, JobsService::class.java).apply {
                action = ACTION_STOP_JOBS
            })
        }
    }

    private var taskManager = TaskManager(this)
    private var coins = 0
    private val prefs: SharedPreferences by lazy { getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE) }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_START_JOBS) {
            Log.d("lol", "start jobs")
            updateServer()
        } else if (intent?.action == ACTION_STOP_JOBS) {
            Log.d("lol", "stop jobs")
            taskManager.abort()
        }
        return START_STICKY
    }

    private fun syncProfile() {
        Log.d("lol", "sync profile")
        InstaApi.getUserInfo(InstaApi.username) { result, error ->
            result?.let { resultNonNull ->
                val isPrivate = resultNonNull.graphql?.user?.isPrivate ?: false

                SyncApi.sync(InstaApi.userId, InstaApi.username, isPrivate) { result, error ->
                    coins = result?.userData?.coins?: 0
                    Log.d("lol", "coins: $coins")

                    val lastOrderTime = prefs.getLong(PREFERENCE_KEY_LAST_ORDER_TIME, 0)
                    val timeToOrder = lastOrderTime + ORDER_INTERVAL - System.currentTimeMillis()
                    val mediaCount = resultNonNull.graphql?.user?.timelineMedia?.count ?: 0

                    if(coins != 0 && mediaCount != 0 && timeToOrder <= 0) createOrder()
                    else getJobs()
                }
            }
            error?.let {
                Log.d("lol", "error", it)
            }
        }
    }

    private fun createOrder() {
        Log.d("lol", "create order")
        InstaApi.getMedia(InstaApi.userId, MEDIA_LIMIT, "") { result, error ->
            result?.let { resultNonNull ->
                val medias = resultNonNull.data.user?.timelineMedia?.edges ?: listOf()

                val mediasWithTag = medias.filter {
                    it.node.mediaCaption.edges.firstOrNull { edge ->
                        edge.node.text.contains(TAG)
                    } != null
                }

                val mediasToOrder = (if(mediasWithTag.isEmpty()) medias else mediasWithTag).toMutableList()
                Log.d("lol", "found ${mediasToOrder.size} medias")
                createOrderWithMedia(mediasToOrder)
            }
            if(error != null) getJobs()
        }
    }

    private fun createOrderWithMedia(medias: MutableList<ProfileInfoModel.Edges>) {
        val orders = arrayListOf<Order>()
        val random = Random()
        val available = coins / ORDER_PRICE
        while (orders.size <= available && orders.size < ORDER_LIMIT && medias.isNotEmpty()) {
            val item = medias[random.nextInt(medias.size)]
            medias.remove(item)

            val itemId = item.node.id
            val shortcode = item.node.shortcode
            val imageSrc = item.node.displayUrl
            val ownerId = InstaApi.userId

            orders.add(Order(itemId, shortcode, imageSrc, ownerId))
        }
        val gson = GsonBuilder().setLenient().create()
        val order = gson.toJson(orders)

        Log.d("lol", "send order")
        SyncApi.addOrder(InstaApi.userId, order) { result, error ->
            Log.d("lol", result?.toString() ?: error?.toString() ?: "empty response")
            prefs.edit().putLong(PREFERENCE_KEY_LAST_ORDER_TIME, System.currentTimeMillis()).apply()
            getJobs()
        }
    }

    override fun onTaskComplete(task: Task) {
        if(taskManager.getTaskLeft() == 0) getJobs()
    }

    override fun onTaskError(task: Task, abort: Boolean, obj: Any?) {
        if(abort) {
            (obj as LikeModel?)?.let { likeModel ->
                if(likeModel.feedbackUrl.isNotEmpty()) {
                }
            }
        }
    }

    private fun getJobs() {
        Log.d("lol", "get jobs")
        if(prefs.getLong(PREFERENCE_KEY_LAST_LIKE_TIME, 0) + LIKE_INTERVAL < System.currentTimeMillis()) {
            prefs.edit().apply{
                putLong(PREFERENCE_KEY_LAST_LIKE_TIME, System.currentTimeMillis())
                    .apply()
            }
            SyncApi.getJobs(InstaApi.userId) { result, _ ->
                Log.d("lol", "found ${result?.jobs?.size} jobs")
                result?.jobs?.forEach {
                    val task = PutLikeTask(it, InstaApi.userId)
                    val delay =
                        (Random().nextInt(LIKE_MAX_INTERVAL - LIKE_MIN_INTERVAL) + LIKE_MIN_INTERVAL) * 1000L
                    taskManager.enqueue(task, delay)
                }
            }
        }
    }

    private fun updateServer() {
        ServerApi.getServerUrl(InstaApi.userId) { result, error ->
            result?.let {
                SyncApi.init(result.server)
                syncProfile()
            }
        }
    }

    override fun onBind(intent: Intent): IBinder {
        return Binder()
    }

    class PutLikeTask(
        private val jobTask: JobsModel.Job,
        private var likerId: Long
    ) : Task() {
        private var actionStack = arrayListOf(
            Action.LOAD_PHOTO,
            Action.LOAD_PROFILE,
            Action.LOAD_POST
        ).shuffle()

        enum class Action {
            LOAD_PHOTO,
            LOAD_PROFILE,
            LOAD_POST,
        }

        override fun execute() {
            loadPost()
        }

        private fun loadProfile() {
            Log.d("lol", "load profile")
            InstaApi.getUserInfo(jobTask.user!!.instaname) { result, error ->
                Log.d("lol", "profile loaded")
                loadPost()
            }
        }

        private fun loadPost() {
            Log.d("lol", "load post")
            InstaApi.getPost(jobTask.item!!.shortcode) { result, error ->
                if(result != null) {
                    Thread.sleep(2000)
                    loadPhoto(result.graphql.shortcodeMedia.displayUrl)
                } else error(false)
            }
        }

        private fun loadPhoto(url: String) {
            Log.d("lol", "load image")
            InstaApi.getRequest(url) { result, error ->
                if(result != null) {
                    Log.d("lol", "image loaded")
                }
                if(error != null) {
                    Log.d("lol", "fail to load image")
                }
                Thread.sleep(2000)
                putLike()
            }
        }

        private fun putLike() {
            InstaApi.like(jobTask.item!!.itemId) { result, _ ->
                Log.d("lol", "#$num like to ${jobTask.item!!.itemId}: ${result?.status}, message: ${result?.message}, spam: ${result?.spam}, feedback: ${result?.feedbackUrl}")
                if (result?.status == "ok") report()
                else error(result?.spam == true, result)
            }
        }

        override fun abort() {

        }

        private fun report() {
            num ++
            SyncApi.redeem(jobTask.id, likerId) { result, error ->
                Log.d("lol", "report code ${result?.errorCode}")
                if (result?.errorCode == 0) success()
                else error()
            }
        }
    }

    data class Order(
        @SerializedName("item_id") val itemId: Long,
        val shortcode: String,
        @SerializedName("img_src") val imageSrc: String,
        @SerializedName("owner_id") val ownerId: Long
    )
}
