package com.faatikhriziq.storyapp.ui.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.faatikhriziq.storyapp.R
import com.faatikhriziq.storyapp.data.source.local.entity.StoryEntity
import com.faatikhriziq.storyapp.databinding.ActivityDetailBinding
import com.faatikhriziq.storyapp.data.repository.Result
import com.faatikhriziq.storyapp.helper.ViewModelFactory
import com.faatikhriziq.storyapp.utils.DateFormatter

@Suppress("DEPRECATION")
class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var viewModel: DetailViewModel

    companion object {
        const val EXTRA_STORY = "extra_story"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.elevation = 0f
        supportActionBar?.setTitle(R.string.detail_story)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val story = intent.getParcelableExtra(EXTRA_STORY) as StoryEntity?

        if (story != null) {
            setupViewModel()
            setupData(story)
            setupAction(story)
        }
    }

    private fun setupAction(story: StoryEntity) {
        binding.swipeRefreshLayout.setOnRefreshListener {
            setupData(story)
        }
    }

    private fun setupData(story: StoryEntity) {
        fabBookmarkAction(story)

        viewModel.getLogin().observe(this) { user ->
            executeGetDetailStory(user.token, story.id)
        }

        binding.swipeRefreshLayout.isRefreshing = false
    }

    private fun executeGetDetailStory(token: String, id: String) {
        viewModel.getDetailStory(token, id).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.tvMessage.visibility = View.GONE
                        binding.cvDetailStory.visibility = View.GONE
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.tvMessage.visibility = View.GONE
                        binding.cvDetailStory.visibility = View.VISIBLE
                        binding.fabDetailSaveBookmark.visibility = View.VISIBLE

                        setData(result.data)
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.cvDetailStory.visibility = View.GONE
                        binding.tvMessage.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun fabBookmarkAction(story: StoryEntity) {
        binding.apply {
            if (story.isBookmarked) {
                fabDetailSaveBookmark.setImageDrawable(
                    ContextCompat.getDrawable(
                    this@DetailActivity,
                    R.drawable.baseline_bookmark_48
                ))
            } else {
                fabDetailSaveBookmark.setImageDrawable(
                    ContextCompat.getDrawable(
                    this@DetailActivity,
                    R.drawable.baseline_bookmark_border_48
                ))
            }

            fabDetailSaveBookmark.setOnClickListener {
                if (story.isBookmarked) {
                    viewModel.deleteStory(story)
                    fabDetailSaveBookmark.setImageDrawable(
                        ContextCompat.getDrawable(
                        this@DetailActivity,
                        R.drawable.baseline_bookmark_border_48
                    ))
                } else {
                    viewModel.saveStory(story)
                    fabDetailSaveBookmark.setImageDrawable(
                        ContextCompat.getDrawable(
                        this@DetailActivity,
                        R.drawable.baseline_bookmark_48
                    ))
                }
            }
        }
    }

    private fun setData(story: StoryEntity) {
        binding.apply {
            Glide
                .with(this@DetailActivity)
                .load(story.photoUrl)
                .into(ivDetailPhoto)

            tvDetailName.text = story.name
            tvDetailDescription.text = story.description
            tvDetailCreatedAt.text = DateFormatter.formatDate(story.createdAt)
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(this)
        )[DetailViewModel::class.java]
    }







}