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
import com.vla.sksu.app.data.BookMeta
import com.vla.sksu.app.databinding.FragmentBookBinding
import com.vla.sksu.app.manager.ServerService
import com.vla.sksu.app.ui.BaseFragment
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Locale

private const val LOG_TAG = "BookFragment"

class BookFragment : BaseFragment() {
    private var _binding: FragmentBookBinding? = null
    private val binding get() = _binding!!

    private val args: BookFragmentArgs by navArgs()
    private var book: Book? = null
    private var bookMeta: BookMeta? = null

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

        binding.buttonNotify.setOnClickListener {
            postNotifyRequest()
        }

        binding.buttonCancel.setOnClickListener {
            postCancelRequest()
        }

        // --

        if (args.book == null && args.bookId != -1) {
            loadBookDetail(args.bookId)
        } else {
            showBookDetail()
            loadBookDetail(args.book?.id ?: 0)
        }
    }

    private fun loadBookDetail(bookId: Int, isRefreshing: Boolean = false) {
        binding.refresh.isRefreshing = isRefreshing
        binding.buttonCancel.isEnabled = false
        binding.buttonBorrow.isEnabled = false
        binding.buttonNotify.isEnabled = false

        binding.loader.visibility = if(isRefreshing.not()) View.VISIBLE else View.GONE

        apiManager?.getBook(bookId) { response ->
            if (response.success) {
                book = response.data?.data
                bookMeta = response.data?.meta

                if (_binding != null) {
                    showBookDetail()
                }
            } else {
                Timber.tag(LOG_TAG).e(response.error)
                showToast(response.getErrorMessage())
            }

            _binding?.refresh?.isRefreshing = false
            _binding?.loader?.visibility = View.GONE
        }
    }

    private fun showBookDetail() {
        val book = book ?: return

        binding.title.text = book.title
        binding.author.text = book.author
        binding.category.text = book.category?.name
        binding.circulation.text = book.circulation
        binding.description.text = book.description
        binding.language.text = book.language
        binding.pages.text = "${book.pages}"
        binding.publisher.text = book.publisher
        binding.datePublished.text = book.datePublished?.let { dateFormatter.format(it) }

        Picasso.get().load(book.getImagePath())
            .placeholder(R.drawable.placeholder_book)
            .into(binding.image)

        if (bookMeta == null) {
            binding.viewAvailable.visibility = View.GONE
            binding.viewNotAvailable.visibility = View.GONE
        }

        val bookMeta = bookMeta ?: return

        if (bookMeta.isAvailable == true) {
            binding.viewAvailable.visibility = View.VISIBLE
            binding.viewNotAvailable.visibility = View.GONE

            when(bookMeta.lastRequestStatus) {
                BookMeta.STATUS_PENDING -> {
                    binding.buttonCancel.visibility = View.VISIBLE
                    binding.buttonCancel.isEnabled = true

                    binding.buttonBorrow.visibility = View.GONE

                    binding.textStatus.visibility = View.VISIBLE
                    binding.textStatus.setText(R.string.message_pending)
                }

                BookMeta.STATUS_APPROVED -> {
                    binding.buttonCancel.visibility = View.GONE

                    binding.buttonBorrow.visibility = View.VISIBLE
                    binding.buttonBorrow.isEnabled = false

                    binding.textStatus.visibility = View.VISIBLE
                    binding.textStatus.setText(R.string.message_borrowed)
                }

                else -> {
                    binding.buttonCancel.visibility = View.GONE

                    if (bookMeta.limitReached == true) {
                        binding.buttonBorrow.visibility = View.VISIBLE
                        binding.buttonBorrow.isEnabled = false

                        binding.textStatus.visibility = View.VISIBLE
                        binding.textStatus.setText(R.string.message_limit_reached)
                    } else {
                        binding.buttonBorrow.visibility = View.VISIBLE
                        binding.buttonBorrow.isEnabled = true

                        binding.textStatus.visibility = View.GONE
                    }
                }
            }
        } else {
            binding.viewAvailable.visibility = View.GONE
            binding.viewNotAvailable.visibility = View.VISIBLE
            binding.buttonNotify.isEnabled = true
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
        val bookId = book?.id ?: return

        binding.loader.visibility = View.VISIBLE
        binding.buttonBorrow.isEnabled = false

        apiManager?.borrow(bookId) { response ->
            if (response.success) {
                AlertDialog.Builder(requireContext())
                    .setMessage(R.string.message_confirmed_borrow)
                    .setNegativeButton(R.string.text_ok) {_, _ -> }
                    .setOnDismissListener {
                        loadBookDetail(book?.id ?: 0)
                    }
                    .create()
                    .show()
            } else if (response.status == ServerService.HTTP_LOCKED) {
                AlertDialog.Builder(requireContext())
                    .setMessage(R.string.message_not_available)
                    .setPositiveButton(R.string.text_ok) {_, _ -> }
                    .setNegativeButton(R.string.text_notify_me) {_, _ ->
                        postNotifyRequest()
                    }
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

            _binding?.loader?.visibility = View.GONE
            _binding?.buttonBorrow?.isEnabled = true
        }
    }

    private fun postNotifyRequest() {
        val bookId = book?.id ?: return

        binding.loader.visibility = View.VISIBLE
        binding.buttonNotify.isEnabled = false

        apiManager?.notify(bookId) { response ->
            if (response.success) {
                AlertDialog.Builder(requireContext())
                    .setMessage(R.string.message_confirmed_notify)
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

            _binding?.loader?.visibility = View.GONE
            _binding?.buttonNotify?.isEnabled = true
        }
    }

    private fun postCancelRequest() {
        val requestId = bookMeta?.lastRequestId ?: return

        binding.loader.visibility = View.VISIBLE
        binding.buttonCancel.isEnabled = false

        apiManager?.cancelRequest(requestId) { response ->
            if (response.success) {
                AlertDialog.Builder(requireContext())
                    .setMessage(R.string.message_cancelled)
                    .setNegativeButton(R.string.text_ok) {_, _ -> }
                    .setOnDismissListener {
                        loadBookDetail(book?.id ?: 0)
                    }
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

            _binding?.loader?.visibility = View.GONE
            _binding?.buttonCancel?.isEnabled = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}