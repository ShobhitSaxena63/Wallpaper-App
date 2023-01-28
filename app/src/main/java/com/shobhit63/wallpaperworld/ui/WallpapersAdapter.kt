package com.shobhit63.wallpaperworld.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shobhit63.wallpaperworld.R
import com.shobhit63.wallpaperworld.data.Wallpapers
import com.shobhit63.wallpaperworld.databinding.RvLookBinding

class WallpapersAdapter(private val listener:(Long)->Unit):ListAdapter<Wallpapers,WallpapersAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//       val itemLayout = LayoutInflater.from(parent.context).inflate(R.layout.rv_look,parent,false)
        val binding = RvLookBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    inner class ViewHolder(private val binding: RvLookBinding):RecyclerView.ViewHolder(binding.root){
        init {
            itemView.setOnClickListener {
                listener.invoke(getItem(adapterPosition).id)
            }
        }
        fun bind(wallpapers: Wallpapers){
            Glide.with(binding.root)
                .load(wallpapers.src.portrait)
                .error(R.drawable.error_image)
                .into(binding.imageView)

        }
    }
}

class DiffCallback:DiffUtil.ItemCallback<Wallpapers>(){
    override fun areItemsTheSame(oldItem: Wallpapers, newItem: Wallpapers): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Wallpapers, newItem: Wallpapers): Boolean {
        return oldItem==newItem
    }

}