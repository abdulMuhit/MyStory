package com.dicoding.mystory.Adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.mystory.DetailStoryActivity
import com.dicoding.mystory.R
import com.dicoding.mystory.models.Story
import com.dicoding.mystory.databinding.ItemStoryBinding
import androidx.core.util.Pair

class StoryAdapter(private val storyList: List<Story>) :
    RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemStoryBinding.inflate(inflater, parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val currentStory = storyList[position]
        holder.bind(currentStory)
    }

    override fun getItemCount(): Int {
        return storyList.size
    }

    inner class StoryViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story: Story) {
            binding.tvDetailName.text = story.name


            Glide.with(binding.root)
                .load(story.photoUrl)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(binding.ivDetailPhoto)

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailStoryActivity::class.java)
                intent.putExtra("STORY_ID", story.id)

                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(binding.tvDetailName, "name"),
                        Pair(binding.tvDetailDescription, "description"),
                        Pair(binding.ivDetailPhoto, "profile")
                    )
                itemView.context.startActivity(intent, optionsCompat.toBundle())
            }
        }
    }
}
