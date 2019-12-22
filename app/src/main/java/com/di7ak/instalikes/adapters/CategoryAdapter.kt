package com.di7ak.instalikes.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.di7ak.instalikes.R
import kotlinx.android.synthetic.main.category_item.view.*

class CategoryAdapter : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {
    var items = listOf<String>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var listener: OnCategoryClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return CategoryViewHolder(inflater.inflate(R.layout.category_item, parent, false))
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.name.text = items[position]
        holder.itemView.setOnClickListener{ listener?.onCategoryClick(position)}
    }

    class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.categoryName
    }

    interface OnCategoryClickListener {
        fun onCategoryClick(num: Int)
    }
}