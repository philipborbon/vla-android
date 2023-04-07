package com.vla.sksu.app.ui.category

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vla.sksu.app.data.Category
import com.vla.sksu.app.databinding.LayoutCategoryBinding

class CategoryAdapter(private val onSelect: (Category) -> Unit) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {
    var dataList: List<Category>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = LayoutCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding, onSelect)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        dataList?.get(position)?.let {
            holder.bind(it)
        }
    }

    override fun getItemCount() = dataList?.size ?: 0

    class CategoryViewHolder(
        private val binding: LayoutCategoryBinding,
        private val onSelect: (Category) -> Unit
    ): RecyclerView.ViewHolder(binding.root) {
        private lateinit var category: Category

        init {
            binding.layout.setOnClickListener {
                onSelect(category)
            }
        }

        fun bind(category: Category) {
            this.category = category

            binding.name.text = category.name
        }
    }
}