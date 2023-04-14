package com.vla.sksu.app.ui.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.vla.sksu.app.R
import com.vla.sksu.app.data.HistoryOverview
import com.vla.sksu.app.databinding.FragmentAccountBinding
import com.vla.sksu.app.ui.BaseFragment
import com.vla.sksu.app.ui.common.MenuAdapter
import com.vla.sksu.app.ui.common.MenuItem
import com.vla.sksu.app.utils.capitalizeWords
import timber.log.Timber

private const val LOG_TAG = "AccountFragment"

class AccountFragment : BaseFragment() {
    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    private var menuAdapter: MenuAdapter? = null
    private val menuList: ArrayList<MenuItem> = arrayListOf()

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
            loadHistoryOverview( true)
        }

        menuAdapter = MenuAdapter()
        menuAdapter?.dataList = menuList

        binding.recycler.adapter = menuAdapter
        binding.recycler.layoutManager = LinearLayoutManager(binding.recycler.context)
        binding.recycler.addItemDecoration(DividerItemDecoration(binding.recycler.context, LinearLayoutManager.VERTICAL))

        loadHistoryOverview()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadHistoryOverview(showLoader: Boolean = false) {
        binding.refresh.isRefreshing = showLoader

        apiManager?.getHistoryOverview { response ->
            if (response.success) {
                response.data?.let {
                    buildMenu(it)
                }
            } else {
                Timber.tag(LOG_TAG).e(response.error)
                showToast(response.getErrorMessage())
            }

            _binding?.refresh?.isRefreshing = false
        }
    }

    private fun buildMenu(data: HistoryOverview) {
        menuList.clear()

        menuList.add(MenuItem(getString(R.string.text_pendings), data.pendings ?: 0) {
            val action = AccountFragmentDirections
                .actionNavAccountToHavHistoryList(getString(R.string.text_pendings).capitalizeWords(), HistoryOverview.KEY_PENDINGS)

            findNavController().navigate(action)
        })

        menuList.add(MenuItem(getString(R.string.text_to_returns), data.toReturn ?: 0) {
            val action = AccountFragmentDirections
                .actionNavAccountToHavHistoryList(getString(R.string.text_to_returns).capitalizeWords(), HistoryOverview.KEY_TO_RETURN)
            findNavController().navigate(action)
        })

        menuList.add(MenuItem(getString(R.string.text_all_requests),) {
            val action = AccountFragmentDirections
                .actionNavAccountToHavHistoryList(getString(R.string.text_all_requests).capitalizeWords(), HistoryOverview.KEY_ALL)
            findNavController().navigate(action)
        })

        menuAdapter?.notifyDataSetChanged()
    }
}