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

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var storiesAdapter: StoriesAdapter
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

//        setupAdapter()
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
            adapter = storiesAdapter
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
        viewModel.getAllStories(token).observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        storiesAdapter.submitList(result.data)
                    }
                    is Result.Error -> {
                        Toast.makeText(
                            context,
                            R.string.failed_to_load_data,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
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
        storiesAdapter = StoriesAdapter()

        recyclerView = binding.rvStories
        recyclerView.apply {
            adapter = storiesAdapter
            layoutManager = linearLayoutManager
        }
    }
}
