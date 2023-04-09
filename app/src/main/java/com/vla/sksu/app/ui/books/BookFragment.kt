package com.vla.sksu.app.ui.books

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.squareup.picasso.Picasso
import com.vla.sksu.app.R
import com.vla.sksu.app.data.Book
import com.vla.sksu.app.data.Category
import com.vla.sksu.app.databinding.FragmentBookBinding
import com.vla.sksu.app.ui.BaseFragment
import java.text.SimpleDateFormat
import java.util.*

class BookFragment : BaseFragment() {
    private var _binding: FragmentBookBinding? = null
    private val binding get() = _binding!!

    private val args: BookFragmentArgs by navArgs()

    private lateinit var book: Book
    private lateinit var category: Category

    private val dateFormatter = SimpleDateFormat("MMMM d, yyyy", Locale.US)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        book = args.book
        category = args.category
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.title.text = book.title
        binding.author.text = book.author
        binding.category.text = category.name
        binding.description.text = book.description
        binding.language.text = book.language
        binding.pages.text = "${book.pages}"
        binding.publisher.text = book.publisher
        binding.datePublished.text = book.datePublished?.let { dateFormatter.format(it) }

        Picasso.get().load(book.getImagePath())
            .placeholder(R.drawable.placeholder_book)
            .into(binding.image)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}