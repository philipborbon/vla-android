package com.vla.sksu.app.ui.account

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.squareup.picasso.Picasso
import com.vla.sksu.app.R
import com.vla.sksu.app.data.Book
import com.vla.sksu.app.data.History
import com.vla.sksu.app.databinding.FragmentHistoryBinding
import com.vla.sksu.app.ui.BaseFragment
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Locale

private const val LOG_TAG = "HistoryFragment"

class HistoryFragment : BaseFragment() {
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val args: HistoryFragmentArgs by navArgs()

    private val book: Book?
        get() = history?.book

    private var history: History? = null

    private val dateFormatter = SimpleDateFormat("MMMM d, yyyy", Locale.US)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        history = args.history
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.refresh.setColorSchemeResources(R.color.material_orange_500, R.color.material_red_500, R.color.material_teal_500)

        binding.refresh.setOnRefreshListener {
            val bookId = args.history?.id ?: args.historyId
            loadHistoryDetail(bookId, true)
        }

        binding.buttonCancel.setOnClickListener {
            confirmCancel()
        }

        if (args.history == null && args.historyId != -1) {
            loadHistoryDetail(args.historyId)
        } else {
            showHistoryDetail()
        }
    }

    private fun loadHistoryDetail(historyId: Int, isRefreshing: Boolean = false, showLoader: Boolean = false) {
        binding.refresh.isRefreshing = isRefreshing
        binding.loader.visibility = if(showLoader) View.VISIBLE else View.GONE

        apiManager?.getHistory(historyId) { response ->
            if (response.success) {
                history = response.data

                if (_binding != null) {
                    showHistoryDetail()
                }
            } else {
                Timber.tag(LOG_TAG).e(response.error)
                showToast(response.getErrorMessage())
            }

            _binding?.refresh?.isRefreshing = false
            _binding?.loader?.visibility = View.GONE
        }
    }

    private fun showHistoryDetail() {
        val book = book ?: return
        val history = history ?: return

        binding.dateDue.text = history.dueAt?.let { dateFormatter.format(it) } ?: "N/A"
        binding.dateRequested.text = history.createdAt?.let { dateFormatter.format(it) } ?: "N/A"
        binding.dateApproved.text = history.approvedAt?.let { dateFormatter.format(it) } ?: "N/A"

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

        when(history.approved) {
            -1 -> {
                binding.status.setText(R.string.text_status_pending)
                binding.status.setTextColor(ContextCompat.getColor(binding.root.context, R.color.material_orange_500))
                binding.buttonCancel.visibility = View.VISIBLE
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun navigateUp() {
        findNavController().navigateUp()
    }

    private fun confirmCancel() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.title_cancel_request)
            .setMessage(R.string.message_confirm_cancel)
            .setPositiveButton(R.string.text_no) { dialog, id -> }
            .setNegativeButton(R.string.text_yes) { dialog, id ->
                postCancelRequest()
            }

        builder.create().show()
    }

    private fun postCancelRequest() {
        val requestId = history?.id ?: return

        binding.loader.visibility = View.VISIBLE
        binding.buttonCancel.isEnabled = false

        apiManager?.cancelRequest(requestId) { response ->
            if (response.success) {
                AlertDialog.Builder(requireContext())
                    .setMessage(R.string.message_cancelled)
                    .setNegativeButton(R.string.text_ok) {_, _ -> }
                    .setOnDismissListener {
                        navigateUp()
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

                _binding?.buttonCancel?.isEnabled = true
            }

            _binding?.loader?.visibility = View.GONE
        }
    }
}