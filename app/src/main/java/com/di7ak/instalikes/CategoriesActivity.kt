package com.di7ak.instalikes

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.crashlytics.android.Crashlytics
import com.di7ak.instalikes.SubcategoryActivity.Companion.CATEGORY_ID
import com.di7ak.instalikes.adapters.CategoryAdapter
import com.di7ak.instalikes.models.CategoriesModel
import com.di7ak.instalikes.net.insta.InstaApi
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_categories.*
import com.google.android.gms.ads.MobileAds

class CategoriesActivity : AppCompatActivity(), CategoryAdapter.OnCategoryClickListener {
    companion object {
        private const val AD_INTERVAL = 1000 * 15
        private const val REQUEST_LOGIN = 0
    }
    private val adapter = CategoryAdapter()
    private var interstitialAd: InterstitialAd? = null
    private var lastAd = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)

        list.layoutManager = LinearLayoutManager(this)
        list.adapter = adapter
        adapter.listener = this

        loadData()

        MobileAds.initialize(
            this

        ) {
            val adRequest = AdRequest.Builder().build()
            adView.loadAd(adRequest)
        }

        interstitialAd = InterstitialAd(this)
        interstitialAd?.adUnitId = "ca-app-pub-4076740979311728/4646661520"

        checkAuth()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_LOGIN) {
            if (resultCode == Activity.RESULT_CANCELED) finish()
            else JobsService.start(this)
        }
    }

    private fun checkAuth() {
        if (!InstaApi.isLoggedIn) {
            startActivityForResult(Intent(this, LoginActivity::class.java), REQUEST_LOGIN)
        } else {
            JobsService.start(this)
        }
    }

    private fun showAd() {
        if(lastAd + AD_INTERVAL < System.currentTimeMillis()) {
            interstitialAd?.loadAd(AdRequest.Builder().build())
            interstitialAd?.show()
            lastAd = System.currentTimeMillis()
        }
    }

    private fun loadData() {
        Thread {
            val data = assets.open("data.json").bufferedReader().use { it.readText() }
            val gson = GsonBuilder().setLenient().create()
            val categories = gson.fromJson(data, CategoriesModel::class.java)
            runOnUiThread {
                setItems(categories.categories.map {
                    it.name
                })
            }
        }.start()
    }

    private fun setItems(items: List<String>) {
        adapter.items = items
    }

    override fun onCategoryClick(num: Int) {
        showAd()
        startActivity(Intent(this, SubcategoryActivity::class.java).apply {
            putExtra(CATEGORY_ID, num)
        })
    }
}
