package com.dicoding.mystory.ViewModel

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.mystory.models.Story
import com.dicoding.mystory.models.StoryApiResponse
import com.dicoding.mystory.network.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivityViewModel : ViewModel() {
    // LiveData untuk menyimpan daftar cerita
    private val _storyListLiveData = MutableLiveData<List<Story>?>()
    val storyListLiveData: MutableLiveData<List<Story>?> get() = _storyListLiveData
    // Fungsi untuk memuat daftar cerita dari API
    fun loadStoryList(sharedPreferences: SharedPreferences) {

        val apiService = ApiConfig().getApiService(sharedPreferences)
        val getAllStory = apiService.getAllStories(1, null, 0)

        getAllStory.enqueue(object : Callback<StoryApiResponse> {
            override fun onResponse(call: Call<StoryApiResponse>, response: Response<StoryApiResponse>) {
                if (response.isSuccessful) {
                    Log.w("GETSTORY", listOf(response.body()).toString())

                    val apiResponse = response.body()
                    if (apiResponse != null) {
                        val storyList = apiResponse.listStory
                        _storyListLiveData.postValue(storyList)

                    }
                }
            }

            override fun onFailure(call: Call<StoryApiResponse>, t: Throwable) {
                Log.e("GETSTORY", t.toString())

            }
        })
    }
}

