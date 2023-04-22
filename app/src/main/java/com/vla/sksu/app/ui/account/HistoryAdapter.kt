package com.vla.sksu.app.ui.account

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.vla.sksu.app.R
import com.vla.sksu.app.data.History
import com.vla.sksu.app.databinding.LayoutHistoryItemBinding
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryAdapter(private val onSelect: (History) -> Unit) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {
    var dataList: ArrayList<History>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = LayoutHistoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding, onSelect)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        dataList?.get(position)?.let {
            holder.bind(it)
        }
    }

    override fun getItemCount() = dataList?.size ?: 0

    class HistoryViewHolder(
        private val binding: LayoutHistoryItemBinding,
        private val onSelect: (History) -> Unit
    ): RecyclerView.ViewHolder(binding.root) {
        private val dateFormatter = SimpleDateFormat("MMMM d, yyyy", Locale.US)

        private lateinit var history: History

        init {
            binding.layout.setOnClickListener {
                onSelect(history)
            }
        }

        fun bind(history: History) {
            this.history = history

            binding.image.setImageResource(R.drawable.placeholder_book)

            if (history.book?.image.isNullOrBlank().not()) {
                Picasso.get().load(history.book?.getImagePath())
                    .placeholder(R.drawable.placeholder_book)
                    .into(binding.image)
            }

            binding.title.text = history.book?.title

            binding.dateDue.text = history.dueAt?.let { dateFormatter.format(it) } ?: "N/A"

            binding.dateRequested.text = history.createdAt?.let { dateFormatter.format(it) }

            when(history.approved) {
                -1 -> {
                    binding.status.setText(R.string.text_status_pending)
                    binding.status.setTextColor(ContextCompat.getColor(binding.root.context, R.color.material_orange_500))
                }

                0 -> {
                    binding.status.setText(R.string.text_status_denied)
                    binding.status.setTextColor(ContextCompat.getColor(binding.root.context, R.color.material_red_500))
                }

                1 -> {
                    if (history.returnedAt == null) {
                        binding.status.setText(R.string.text_status_approved)
                        binding.status.setTextColor(ContextCompat.getColor(binding.root.context, R.color.material_green_500))
                    } else {
                        binding.status.setText(R.string.text_status_returned)
                        binding.status.setTextColor(ContextCompat.getColor(binding.root.context, R.color.material_grey_600))
                    }
                }
            }
        }
    }
}