package com.dicoding.mystory.models


data class Story(
    val id: String,
    val name: String,
    val description: String,
    val photoUrl: String,
    val createdAt: String,
    val lat: Double,
    val lon: Double
)

data class StoryApiResponse(
    val error: Boolean,
    val message: String,
    val listStory: List<Story>
)

data class StoryDetailApiResponse(
    val error: Boolean,
    val message: String,
    val story: Story
)