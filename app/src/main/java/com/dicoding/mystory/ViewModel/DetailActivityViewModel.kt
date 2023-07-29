package com.dicoding.mystory.ViewModel

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.mystory.models.Story
import com.dicoding.mystory.models.StoryDetailApiResponse
import com.dicoding.mystory.network.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailActivityViewModel : ViewModel() {

    private val _storyLiveData = MutableLiveData<Story>()
    val storyLiveData: MutableLiveData<Story>? get() = _storyLiveData

    // Fungsi untuk memuat cerita dari API
    fun loadStory(sharedPreferences: SharedPreferences, id: String) {
        val apiService = ApiConfig().getApiService(sharedPreferences)
        val getStory = apiService.getDetailStory(id)

        getStory.enqueue(object: Callback<StoryDetailApiResponse> {
            override fun onResponse(call: Call<StoryDetailApiResponse>, response: Response<StoryDetailApiResponse>) {
                if (response.isSuccessful) {
                    Log.w("GETSTORYDETAIL", listOf(response.body()).toString())

                    val apiResponse = response.body()
                    if (apiResponse != null) {
                        val storyList = apiResponse.story
                        _storyLiveData.postValue(storyList)
                    }
                }
            }

            override fun onFailure(call: Call<StoryDetailApiResponse>, t: Throwable) {
                Log.e("GETSTORYDETAIL", t.toString())
            }
        })
    }

}

