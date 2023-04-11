package com.vla.sksu.app.ui.books

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.vla.sksu.app.R
import com.vla.sksu.app.data.Category
import com.vla.sksu.app.databinding.FragmentBookListingBinding
import com.vla.sksu.app.ui.BaseFragment
import timber.log.Timber

private const val LOG_TAG = "BookFragment"

class BookListingFragment : BaseFragment() {
    private var _binding: FragmentBookListingBinding? = null
    private val binding get() = _binding!!

    private var booksAdapter: BookListAdapter? = null

    private val args: BookListingFragmentArgs by navArgs()

    private lateinit var category: Category

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        category = args.category
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookListingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.refresh.setColorSchemeResources(R.color.material_orange_500, R.color.material_red_500, R.color.material_teal_500)

        binding.refresh.setOnRefreshListener {
            loadBooks(args.category.id ?: 0, true)
        }

        booksAdapter = BookListAdapter {
            val action = BookListingFragmentDirections.actionNavBooksToNavBook(it)
            findNavController().navigate(action)
        }

        binding.recycler.adapter = booksAdapter
        binding.recycler.layoutManager = GridLayoutManager(binding.recycler.context, 3)

        binding.category.text = category.name

        loadBooks(args.category.id ?: 0)
    }

    private fun loadBooks(categoryId: Int, showLoader: Boolean = false) {
        binding.refresh.isRefreshing = showLoader

        apiManager?.getBooks(categoryId) { response ->
            if (response.success) {
                booksAdapter?.dataList = response.data
                booksAdapter?.notifyDataSetChanged()

                _binding?.labelEmpty?.visibility = if (response.data?.isEmpty() == true) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            } else {
                Timber.tag(LOG_TAG).e(response.error)
                showToast(response.getErrorMessage())
            }

            _binding?.refresh?.isRefreshing = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}