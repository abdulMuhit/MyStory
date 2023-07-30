package com.dicoding.mystory

import android.app.ProgressDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.mystory.databinding.ActivityMainBinding
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.mystory.Adapter.StoryAdapter
import com.dicoding.mystory.ViewModel.MainActivityViewModel
import com.dicoding.mystory.models.Story

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var recyclerView: RecyclerView
    private lateinit var storyAdapter: StoryAdapter
    private val viewModel: MainActivityViewModel by viewModels()
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Story"
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Logging in...")
        progressDialog.setCancelable(false)

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        progressDialog.show()
        viewModel.loadStoryList(sharedPreferences)

        viewModel.storyListLiveData.observe(this, Observer { storyList ->
            // Update RecyclerView dengan daftar cerita terbaru
            if (storyList != null) {
                updateRecyclerView(storyList)
                progressDialog.dismiss()
            }
        })

        binding.btnCreateStory.setOnClickListener {
            // Start CreateStoryActivity when the button is clicked
            val moveForResultIntent = Intent(this@MainActivity, CreateStoryActivity::class.java)
            resultLauncher.launch(moveForResultIntent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                // Menghapus informasi token dan sesi
                sharedPreferences.edit().remove("isLoggedIn").apply()
                sharedPreferences.edit().remove("token").apply()

                // Arahkan pengguna kembali ke halaman login
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Fungsi untuk memperbarui RecyclerView dengan daftar cerita terbaru
    private fun updateRecyclerView(storyList: List<Story>) {
        // Implementasikan pembaruan RecyclerView di sini
        storyAdapter = StoryAdapter(storyList)
        recyclerView.adapter = storyAdapter
        storyAdapter.notifyDataSetChanged()
    }

    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == CreateStoryActivity.RESULT_CODE && result.data != null) {
            val selectedValue =
                result.data?.getIntExtra(CreateStoryActivity.EXTRA_SELECTED_VALUE, 0)
            Log.d("RESULTlAUNCHER", selectedValue.toString())
            progressDialog.show()
            viewModel.loadStoryList(sharedPreferences)
        }
    }
}
