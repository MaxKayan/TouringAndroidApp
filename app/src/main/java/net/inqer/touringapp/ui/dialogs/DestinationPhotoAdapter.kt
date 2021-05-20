package net.inqer.touringapp.ui.dialogs

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import net.inqer.touringapp.data.models.DestinationPhoto
import net.inqer.touringapp.databinding.ItemInfoPhotoBinding

class DestinationPhotoAdapter(
    private val onClick: (photo: DestinationPhoto, imageView: ImageView) -> Unit
) :
    ListAdapter<DestinationPhoto, DestinationPhotoAdapter.PhotoViewHolder>(
        DESTINATION_PHOTO_ITEM_CALLBACK
    ) {


    class PhotoViewHolder(
        private val binding: ItemInfoPhotoBinding,
        private val onClick: (photo: DestinationPhoto, imageView: ImageView) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(photo: DestinationPhoto) {
            Glide.with(binding.root)
                .load(photo.url)
                .placeholder(android.R.drawable.progress_indeterminate_horizontal)
                .into(binding.imageView)

            binding.imageView.setOnClickListener {
                onClick(photo, it as ImageView)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding: ItemInfoPhotoBinding =
            ItemInfoPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return PhotoViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DESTINATION_PHOTO_ITEM_CALLBACK: DiffUtil.ItemCallback<DestinationPhoto> =
            object : DiffUtil.ItemCallback<DestinationPhoto>() {
                override fun areItemsTheSame(
                    oldItem: DestinationPhoto,
                    newItem: DestinationPhoto
                ): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(
                    oldItem: DestinationPhoto,
                    newItem: DestinationPhoto
                ): Boolean {
                    return oldItem == newItem
                }

            }
    }
}