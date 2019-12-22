package com.di7ak.instalikes.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.di7ak.instalikes.R
import kotlinx.android.synthetic.main.category_item.view.*

class SubcategoryAdapter : RecyclerView.Adapter<SubcategoryAdapter.SubcategoryViewHolder>() {
    var items = listOf<String>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var listener: OnSubcategoryClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubcategoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return SubcategoryViewHolder(inflater.inflate(R.layout.category_item, parent, false))
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: SubcategoryViewHolder, position: Int) {
        holder.name.text = items[position]
        holder.itemView.setOnClickListener{ listener?.onSubcategoryClick(position)}
    }

    class SubcategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.categoryName
    }

    interface OnSubcategoryClickListener {
        fun onSubcategoryClick(num: Int)
    }
}