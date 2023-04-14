package com.vla.sksu.app.ui.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vla.sksu.app.databinding.LayoutMenuItemBinding

class MenuAdapter : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {
    var dataList: ArrayList<MenuItem>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = LayoutMenuItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        dataList?.get(position)?.let {
            holder.bind(it)
        }
    }

    override fun getItemCount() = dataList?.size ?: 0

    class MenuViewHolder(private val binding: LayoutMenuItemBinding): RecyclerView.ViewHolder(binding.root) {
        private lateinit var item: MenuItem

        init {
            binding.layout.setOnClickListener {
                item.action()
            }
        }

        fun bind(item: MenuItem) {
            this.item = item
            binding.name.text = item.name
            binding.count.text = "${item.count}"

            binding.count.visibility = if (item.count == 0) View.GONE else View.VISIBLE
        }
    }
}