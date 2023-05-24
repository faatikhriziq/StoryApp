package com.faatikhriziq.storyapp.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.faatikhriziq.storyapp.databinding.ActivityLoginBinding
import com.faatikhriziq.storyapp.helper.ViewModelFactory
import com.faatikhriziq.storyapp.ui.home.HomeActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupFragment()
        
    }


    private fun setupFragment() {
        viewModel.getLogin().observe(this) { user ->
            if (user.token.isNotBlank()) {
                directToHomeActivity()
            }
        }
    }


    private fun directToHomeActivity() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }



    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(this)
        )[LoginViewModel::class.java]
    }
}