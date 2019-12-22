package com.di7ak.instalikes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.di7ak.instalikes.SubcategoryActivity.Companion.CATEGORY_ID
import com.di7ak.instalikes.adapters.CategoryAdapter
import com.di7ak.instalikes.models.CategoriesModel
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_categories.*

class CategoriesActivity : AppCompatActivity(), CategoryAdapter.OnCategoryClickListener {
    private val adapter = CategoryAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)

        list.layoutManager = LinearLayoutManager(this)
        list.adapter = adapter
        adapter.listener = this

        loadData()
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
        startActivity(Intent(this, SubcategoryActivity::class.java).apply {
            putExtra(CATEGORY_ID, num)
        })
    }
}
