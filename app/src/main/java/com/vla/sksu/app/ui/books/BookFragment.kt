package com.vla.sksu.app.ui.books

import android.app.AlertDialog
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
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

private const val LOG_TAG = "BookFragment"

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

        // --

        binding.buttonBorrow.setOnClickListener {
            confirmBorrow()
        }
    }

    private fun confirmBorrow() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.title_request_borrow)
            .setMessage(R.string.message_confirm_borrow)
            .setPositiveButton(R.string.text_continue) { dialog, id ->
                postBorrowRequest()
            }
            .setNegativeButton(R.string.text_cancel) { dialog, id ->
                dialog.dismiss()
            }

        builder.create().show()
    }

    private fun postBorrowRequest() {
        val bookId = book.id ?: return

        binding.progress.visibility = View.VISIBLE
        binding.buttonBorrow.isEnabled = false

        apiManager?.borrow(bookId) { response ->
            if (response.success) {
                AlertDialog.Builder(requireContext())
                    .setMessage(R.string.message_confirmed_borrow)
                    .setNegativeButton(R.string.text_ok) {_, _ -> }
                    .create()
                    .show()
            } else {
                Timber.tag(LOG_TAG).e(response.error)

                AlertDialog.Builder(requireContext())
                    .setMessage(response.getErrorMessage())
                    .setNegativeButton(R.string.text_ok) {_, _ -> }
                    .create()
                    .show()
            }

            _binding?.progress?.visibility = View.GONE
            _binding?.buttonBorrow?.isEnabled = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}