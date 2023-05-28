package com.faatikhriziq.storyapp.ui.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.faatikhriziq.storyapp.R
import com.faatikhriziq.storyapp.databinding.ActivityHomeBinding
import com.faatikhriziq.storyapp.helper.ViewModelFactory
import com.faatikhriziq.storyapp.ui.auth.LoginActivity
import com.faatikhriziq.storyapp.ui.create.CreateStoryActivity
import com.faatikhriziq.storyapp.ui.map.MapActivity

class HomeActivity : AppCompatActivity() {


    private lateinit var binding: ActivityHomeBinding
    private lateinit var viewModel: HomeViewModel

    //    private val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val fragment = HomeFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        setupViewModel()

        binding.fabCreateStory.setOnClickListener {
            startActivity(Intent(this, CreateStoryActivity::class.java))
        }


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(this)
        )[HomeViewModel::class.java]
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_logout -> {
                showLogoutDialog()
                true
            }

            R.id.menu_maps -> {
                startActivity(Intent(this, MapActivity::class.java))
                true
            }

            R.id.menu_setting -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showLogoutDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.sign_out)
            .setMessage(R.string.are_you_sure)
            .setPositiveButton(R.string.ok) { _, _ ->
                viewModel.deleteLogin()
                directToMainActivity()
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }

        val alert = builder.create()
        alert.show()
    }

    private fun directToMainActivity() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

}