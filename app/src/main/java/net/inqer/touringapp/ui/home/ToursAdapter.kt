package net.inqer.touringapp.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import net.inqer.touringapp.data.models.TourRouteBrief
import net.inqer.touringapp.databinding.ItemTourBinding

class ToursAdapter constructor(
        private val callbacks: TourViewHolder.OnTourViewInteraction
) : ListAdapter<TourRouteBrief, ToursAdapter.Companion.TourViewHolder>(TOUR_BRIEF_ITEM_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TourViewHolder {
        val binding: ItemTourBinding = ItemTourBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TourViewHolder(binding)
    }


    override fun onBindViewHolder(holder: TourViewHolder, position: Int) {
        holder.bind(getItem(position), callbacks)
    }

    companion object {
        private const val TAG = "ToursAdapter"

        class TourViewHolder constructor(
                private val binding: ItemTourBinding
        ) : RecyclerView.ViewHolder(binding.root) {

            interface OnTourViewInteraction {
                fun click(item: TourRouteBrief)
            }

            fun bind(tour: TourRouteBrief, callbacks: OnTourViewInteraction) {
                binding.title.text = tour.title
                binding.secondaryText.text = tour.created_at
                binding.supportingText.text = tour.description

                Glide.with(binding.root)
                        .load(tour.image)
                        .into(binding.image)

                binding.root.setOnClickListener {
                    callbacks.click(tour)
                }
            }

        }

        private val TOUR_BRIEF_ITEM_CALLBACK: DiffUtil.ItemCallback<TourRouteBrief> = object : DiffUtil.ItemCallback<TourRouteBrief>() {
            override fun areItemsTheSame(oldItem: TourRouteBrief, newItem: TourRouteBrief): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: TourRouteBrief, newItem: TourRouteBrief): Boolean {
                return false
            }
        }
    }
}