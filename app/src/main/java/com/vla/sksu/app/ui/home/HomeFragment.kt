package com.vla.sksu.app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.vla.sksu.app.databinding.FragmentHomeBinding
import com.vla.sksu.app.ui.BaseFragment
import timber.log.Timber

private const val LOG_TAG = "HomeFragment"

class HomeFragment : BaseFragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var searchAdapter: SearchAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchAdapter = SearchAdapter {
            val action = HomeFragmentDirections.actionNavHomeToNavBook(it)
            findNavController().navigate(action)
        }

        binding.recycler.adapter = searchAdapter
        binding.recycler.layoutManager = LinearLayoutManager(binding.recycler.context)
        binding.recycler.addItemDecoration(DividerItemDecoration(binding.recycler.context, LinearLayoutManager.VERTICAL))

        binding.buttonSearch.setOnClickListener {
            loadSearch(binding.inputSearch.text.toString())
            hideKeyboard()
        }

        binding.inputSearch.setOnEditorActionListener { view, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                loadSearch(binding.inputSearch.text.toString())

                return@setOnEditorActionListener true
            }

            return@setOnEditorActionListener false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadSearch(keyword: String) {
        binding.progress.visibility = View.VISIBLE

        apiManager?.search(keyword) { response ->
            if (response.success) {
                searchAdapter?.dataList = response.data
                searchAdapter?.notifyDataSetChanged()

                _binding?.labelEmpty?.visibility = if (response.data?.isEmpty() == true) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            } else {
                Timber.tag(LOG_TAG).e(response.error)
                showToast(response.getErrorMessage())
            }

            _binding?.progress?.visibility = View.GONE
        }
    }
}