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
    private var book: Book? = null

    private val dateFormatter = SimpleDateFormat("MMMM d, yyyy", Locale.US)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        book = args.book
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

        binding.refresh.setColorSchemeResources(R.color.material_orange_500, R.color.material_red_500, R.color.material_teal_500)

        binding.refresh.setOnRefreshListener {
            val bookId = args.book?.id ?: args.bookId
            loadBookDetail(bookId, true)
        }

        binding.buttonBorrow.setOnClickListener {
            confirmBorrow()
        }

        // --

        if (args.book == null && args.bookId != -1) {
            loadBookDetail(args.bookId)
        } else {
            showBookDetail()
        }
    }

    private fun loadBookDetail(bookId: Int, showLoader: Boolean = false) {
        binding.refresh.isRefreshing = showLoader
        binding.buttonBorrow.isEnabled = false

        apiManager?.getBook(bookId) { response ->
            if (response.success) {
                book = response.data

                if (_binding != null) {
                    showBookDetail()
                }
            } else {
                Timber.tag(LOG_TAG).e(response.error)
                showToast(response.getErrorMessage())
            }

            _binding?.refresh?.isRefreshing = false
            _binding?.buttonBorrow?.isEnabled = true
        }
    }

    private fun showBookDetail() {
        val book = book ?: return

        binding.title.text = book.title
        binding.author.text = book.author
        binding.category.text = book.category?.name
        binding.description.text = book.description
        binding.language.text = book.language
        binding.pages.text = "${book.pages}"
        binding.publisher.text = book.publisher
        binding.datePublished.text = book.datePublished?.let { dateFormatter.format(it) }

        Picasso.get().load(book.getImagePath())
            .placeholder(R.drawable.placeholder_book)
            .into(binding.image)
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
        val bookId = book?.id ?: return

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