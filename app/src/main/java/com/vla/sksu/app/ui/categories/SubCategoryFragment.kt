package com.vla.sksu.app.ui.categories

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.vla.sksu.app.R
import com.vla.sksu.app.data.Category

class SubCategoryFragment: CategoryFragment() {
    private val args: SubCategoryFragmentArgs by navArgs()
    private var parent: Category? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        parent = args.parent
    }

    override fun setupView() {
        binding.refresh.setColorSchemeResources(R.color.material_orange_500, R.color.material_red_500, R.color.material_teal_500)

        binding.refresh.setOnRefreshListener {
            loadCategories(args.parent?.id ?: 0, true)
        }

        categoryAdapter = CategoryAdapter {
            val action = if ((it.categories ?:0) > 0) {
                SubCategoryFragmentDirections.actionNavCategory(it)
            } else {
                SubCategoryFragmentDirections.actionNavCategoryToNavBooks(it)
            }

            findNavController().navigate(action)
        }

        binding.recycler.adapter = categoryAdapter
        binding.recycler.layoutManager = LinearLayoutManager(binding.recycler.context)
        binding.recycler.addItemDecoration(DividerItemDecoration(binding.recycler.context, LinearLayoutManager.VERTICAL))

        binding.category.text = parent?.name ?: ""
        binding.category.visibility = if (parent == null) View.GONE else View.VISIBLE

        loadCategories(args.parent?.id ?: 0)
    }
}