package com.dicoding.mystory

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.dicoding.mystory.ViewModel.DetailActivityViewModel
import com.dicoding.mystory.databinding.ActivityDetailStoryBinding

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding
    private lateinit var sharedPreferences: SharedPreferences
    private val viewModel: DetailActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mengambil data dari Intent
        val storyId = intent.getStringExtra("STORY_ID")

        if (storyId != null) {
            viewModel.loadStory(sharedPreferences, storyId)
        }

        viewModel.storyLiveData?.observe(this, Observer { story ->
            if (story != null) {
                // Menampilkan data ke tampilan DetailActivity
                binding.tvDetailName.text = story.name
                binding.tvDetailDescription.text = story.description

                Glide.with(this)
                    .load(story.photoUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(binding.ivDetailPhoto)
            }
        })
        //
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // This will navigate back to the previous activity
                onBackPressed()
                return true
            }
            R.id.action_logout -> {
                // Menghapus informasi token dan sesi
                sharedPreferences.edit().remove("isLoggedIn").apply()
                sharedPreferences.edit().remove("token").apply()

                // Arahkan pengguna kembali ke halaman login
                val intent = Intent(this@DetailStoryActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}