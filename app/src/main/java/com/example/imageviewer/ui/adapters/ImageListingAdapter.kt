package com.example.imageviewer.ui.adapters


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.imageloader.ImageLoader
import com.example.imageviewer.databinding.ImageListingItemLayoutBinding
import com.example.imageviewer.ui.pojos.Children
import com.example.imageviewer.ui.pojos.DataX


class ImageListingAdapter :
    ListAdapter<Children, ImageListingAdapter.ViewHolder>(ImageDiffCallback()) {
    private var mContext: Context? = null
    private var itemCallback: ItemCallback? = null

    fun setItemCallBack(itemCallback: ItemCallback) {
        this.itemCallback = itemCallback
    }

    class ImageDiffCallback : DiffUtil.ItemCallback<Children>() {
        override fun areItemsTheSame(oldItem: Children, newItem: Children): Boolean {
            return oldItem.data.thumbnail == newItem.data.thumbnail
        }

        override fun areContentsTheSame(oldItem: Children, newItem: Children): Boolean {
            return oldItem == newItem
        }

    }

    inner class ViewHolder(val binding: ImageListingItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.name.isSelected=true
            binding.imageCard.setOnClickListener {
                val item = getItem(layoutPosition).data.preview.images[0].resolutions
                val listLength= item.size
                itemCallback?.clickItemCallback(item[listLength-1].url)
            }
        }

        fun bind(item: DataX) {
            //Using Image Loader to load images
            ImageLoader.with(mContext)
                .load(item.thumbnail, binding.thumbnail)
            //image title set below image
            binding.name.text=item.title

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context
        val mInflater = LayoutInflater.from(mContext)
        val binding = ImageListingItemLayoutBinding.inflate(mInflater, parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val image = getItem(position) ?: return
        holder.bind(image.data)
    }

    interface ItemCallback {
        fun clickItemCallback(imageUrl: String?)
    }

}