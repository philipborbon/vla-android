package com.vla.sksu.app.ui.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.vla.sksu.app.R
import com.vla.sksu.app.databinding.FragmentAccountBinding
import com.vla.sksu.app.ui.BaseFragment
import timber.log.Timber

private const val LOG_TAG = "AccountFragment"

class AccountFragment : BaseFragment() {
    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    private var historyAdapter: HistoryAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.refresh.setColorSchemeResources(R.color.material_orange_500, R.color.material_red_500, R.color.material_teal_500)

        binding.refresh.setOnRefreshListener {
            loadHistory( true)
        }

        historyAdapter = HistoryAdapter {
            // History item click action
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

    private fun loadHistory(showLoader: Boolean = false) {
        binding.refresh.isRefreshing = showLoader

        apiManager?.getHistories { response ->
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