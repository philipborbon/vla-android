package com.vla.sksu.app.ui.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.vla.sksu.app.R
import com.vla.sksu.app.databinding.FragmentHistoryListBinding
import com.vla.sksu.app.ui.BaseFragment
import timber.log.Timber

private const val LOG_TAG = "HistoryListFragment"

class HistoryListFragment : BaseFragment() {
    private var _binding: FragmentHistoryListBinding? = null
    private val binding get() = _binding!!

    private var historyAdapter: HistoryAdapter? = null

    private val args: HistoryListFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.refresh.setColorSchemeResources(R.color.material_orange_500, R.color.material_red_500, R.color.material_teal_500)

        binding.refresh.setOnRefreshListener {
            loadHistory( true)
        }

        historyAdapter = HistoryAdapter {
            val action = HistoryListFragmentDirections.actionHavHistoryListToNavHistory(it)
            findNavController().navigate(action)
        }

        binding.recycler.adapter = historyAdapter
        binding.recycler.layoutManager = LinearLayoutManager(binding.recycler.context)
        binding.recycler.addItemDecoration(DividerItemDecoration(binding.recycler.context, LinearLayoutManager.VERTICAL))

        loadHistory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadHistory(isRefreshing: Boolean = false) {
        binding.refresh.isRefreshing = isRefreshing

        apiManager?.getHistories(args.key) { response ->
            if (response.success) {
                historyAdapter?.dataList = response.data
                historyAdapter?.notifyDataSetChanged()

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
}