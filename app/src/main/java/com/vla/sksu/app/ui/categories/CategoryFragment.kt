package com.vla.sksu.app.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.vla.sksu.app.R
import com.vla.sksu.app.data.Category
import com.vla.sksu.app.databinding.FragmentCategoryBinding
import com.vla.sksu.app.ui.BaseFragment
import timber.log.Timber

private const val LOG_TAG = "CategoryFragment"

class CategoryFragment : BaseFragment() {
    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!

    private var categoryAdapter: CategoryAdapter? = null

    private val args: CategoryFragmentArgs by navArgs()

    private var parent: Category? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        parent = args.parent
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.refresh.setColorSchemeResources(R.color.material_orange_500, R.color.material_red_500, R.color.material_teal_500)

        binding.refresh.setOnRefreshListener {
            loadCategories(args.parent?.id ?: 0, true)
        }

        categoryAdapter = CategoryAdapter {
            val action = if ((it.categories ?:0) > 0) {
                CategoryFragmentDirections.actionNavCategory(it)
            } else {
                CategoryFragmentDirections.actionNavCategoryToNavBooks(it)
            }

            findNavController().navigate(action)
        }

        binding.recycler.adapter = categoryAdapter
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        binding.recycler.addItemDecoration(DividerItemDecoration(binding.recycler.context, LinearLayoutManager.VERTICAL))

        binding.category.text = parent?.name ?: ""
        binding.category.visibility = if (parent == null) View.GONE else View.VISIBLE

        loadCategories(args.parent?.id ?: 0)
    }

    private fun loadCategories(parent: Int, showLoader: Boolean = false) {
        binding.refresh.isRefreshing = showLoader

        when (parent) {
            0 -> {
                apiManager?.getCategories { response ->
                    if (response.success) {
                        categoryAdapter?.dataList = response.data
                        categoryAdapter?.notifyDataSetChanged()
                    } else {
                        Timber.tag(LOG_TAG).e(response.error)
                        showToast(response.getErrorMessage())
                    }

                    binding.refresh.isRefreshing = false
                }
            }

            else -> {
                apiManager?.getSubCategories(parent) { response ->
                    if (response.success) {
                        categoryAdapter?.dataList = response.data
                        categoryAdapter?.notifyDataSetChanged()
                    } else {
                        Timber.tag(LOG_TAG).e(response.error)
                        showToast(response.getErrorMessage())
                    }

                    binding.refresh.isRefreshing = false
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}