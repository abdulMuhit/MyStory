package com.dicoding.mystory.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.mystory.R
import com.dicoding.mystory.models.Story
import com.dicoding.mystory.databinding.ItemStoryBinding

class StoryAdapter(private val storyList: List<Story>) :
    RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemStoryBinding.inflate(inflater, parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val currentStory = storyList[position]
        holder.bind(currentStory)
        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(storyList[holder.adapterPosition]) }
    }

    override fun getItemCount(): Int {
        return storyList.size
    }

    inner class StoryViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(story: Story) {
            binding.tvItemName.text = story.name

            Glide.with(binding.root)
                .load(story.photoUrl)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(binding.ivItemPhoto)
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Story)
    }
}
