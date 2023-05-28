package com.faatikhriziq.storyapp.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.faatikhriziq.storyapp.R
import com.faatikhriziq.storyapp.databinding.FragmentHomeBinding
import com.faatikhriziq.storyapp.ui.adapter.StoriesAdapter
import com.faatikhriziq.storyapp.data.repository.Result
import com.faatikhriziq.storyapp.helper.ViewModelFactory
import com.faatikhriziq.storyapp.ui.adapter.LoadingStateAdapter
import com.faatikhriziq.storyapp.ui.adapter.StoriesHomeAdapter

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var storiesHomeAdapter: StoriesHomeAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRecyclerView()
        setupViewModel()
        setupData()
        setData()
        setupAction()
    }

    private fun setupAction() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            setupData()
        }
    }

    private fun setData() {
        binding.rvStories.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = storiesHomeAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    storiesHomeAdapter.retry()
                }
            )
        }
    }

    private fun setupData() {
        viewModel.getLogin().observe(viewLifecycleOwner) { user ->
            if (user.token.isNotBlank()) {
                executeGetAllStories(user.token)
            }
        }

        binding.swipeRefreshLayout.isRefreshing = false
    }

    private fun executeGetAllStories(token: String) {
        viewModel.getAllStories(token).observe(viewLifecycleOwner) {
            storiesHomeAdapter.submitData(lifecycle, it)
        }
    }


    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(requireContext())
        )[HomeViewModel::class.java]
    }

    private fun setRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        storiesHomeAdapter = StoriesHomeAdapter()

        recyclerView = binding.rvStories
        recyclerView.apply {
            adapter = storiesHomeAdapter
            layoutManager = linearLayoutManager
        }
    }
}

