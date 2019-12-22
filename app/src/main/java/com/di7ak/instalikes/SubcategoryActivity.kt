package com.di7ak.instalikes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.di7ak.instalikes.adapters.SubcategoryAdapter
import com.di7ak.instalikes.models.CategoriesModel
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_subcategory.*
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.view.View
import com.google.android.material.snackbar.Snackbar
import android.net.Uri
import com.di7ak.instalikes.net.insta.InstaApi


class SubcategoryActivity : AppCompatActivity(), SubcategoryAdapter.OnSubcategoryClickListener {
    companion object {
        const val CATEGORY_ID = "category_id"
    }

    private val adapter = SubcategoryAdapter()
    private var tags: MutableList<String> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subcategory)

        list.layoutManager = LinearLayoutManager(this)
        list.adapter = adapter
        adapter.listener = this

        loadData(intent.getIntExtra(CATEGORY_ID, 0))
    }

    private fun loadData(categoryId: Int) {
        Thread {
            val data = assets.open("data.json").bufferedReader().use { it.readText() }
            val gson = GsonBuilder().setLenient().create()
            val subCategories = gson.fromJson(data, CategoriesModel::class.java)
            runOnUiThread {
                setItems(subCategories.categories[categoryId].subcategories.map {
                    tags.add(it.tags)
                    it.name
                })
            }
        }.start()
    }

    private fun setItems(items: List<String>) {
        adapter.items = items
    }

    override fun onSubcategoryClick(num: Int) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("label", tags[num])
        clipboard.setPrimaryClip(clip)

        Snackbar
            .make(root, getString(com.di7ak.instalikes.R.string.text_copied), Snackbar.LENGTH_LONG)
            .setAction(getString(com.di7ak.instalikes.R.string.open_insta)) {
                val url = "https://www.instagram.com/${InstaApi.username}/"
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                startActivity(i)
            }.show()
    }
}
