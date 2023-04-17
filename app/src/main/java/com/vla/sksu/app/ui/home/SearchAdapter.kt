package com.vla.sksu.app.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.vla.sksu.app.R
import com.vla.sksu.app.data.Book
import com.vla.sksu.app.databinding.LayoutSearchItemBinding

class SearchAdapter(private val onSelect: (Book) -> Unit) : RecyclerView.Adapter<SearchAdapter.BookViewHolder>() {
    var dataList: ArrayList<Book>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = LayoutSearchItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookViewHolder(binding, onSelect)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        dataList?.get(position)?.let {
            holder.bind(it)
        }
    }

    override fun getItemCount() = dataList?.size ?: 0

    class BookViewHolder(
        private val binding: LayoutSearchItemBinding,
        private val onSelect: (Book) -> Unit
    ): RecyclerView.ViewHolder(binding.root) {
        private lateinit var book: Book

        init {
            binding.layout.setOnClickListener {
                onSelect(book)
            }
        }

        fun bind(book: Book) {
            this.book = book

            binding.image.setImageResource(R.drawable.placeholder_book)

            if (book.image.isNullOrBlank().not()) {
                Picasso.get().load(book.getImagePath())
                    .placeholder(R.drawable.placeholder_book)
                    .into(binding.image)
            }

            binding.title.text = book.title
            binding.category.text = book.category?.path
            binding.author.text = book.author
            binding.publisher.text = book.publisher
        }
    }
}