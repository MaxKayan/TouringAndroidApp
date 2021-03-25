package net.inqer.touringapp.ui.home

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.circularreveal.CircularRevealCompat
import com.google.android.material.circularreveal.CircularRevealWidget
import net.inqer.touringapp.R
import net.inqer.touringapp.data.models.TourRouteBrief
import net.inqer.touringapp.databinding.ItemTourBinding
import net.inqer.touringapp.util.DrawableHelper
import kotlin.math.sqrt


class ToursAdapter constructor(
        private val callbacks: TourViewHolder.OnTourViewInteraction
) : ListAdapter<TourRouteBrief, ToursAdapter.Companion.TourViewHolder>(TOUR_BRIEF_ITEM_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TourViewHolder {
        val binding: ItemTourBinding = ItemTourBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TourViewHolder(binding)
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
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

            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            fun bind(tour: TourRouteBrief, callbacks: OnTourViewInteraction) {
//                binding.root.visibility = View.INVISIBLE
                binding.title.text = tour.title
                binding.secondaryText.text = tour.created_at
                binding.supportingText.text = tour.description

                binding.innerTitle.text = tour.title
                binding.innerSubtitle.text = tour.created_at

                Glide.with(binding.root)
                        .load(tour.image)
                        .into(binding.image)

                binding.root.setOnClickListener {
                    callbacks.click(tour)
                }

                binding.fabTour.setOnClickListener {
                    animateCircularReveal(binding.innerCard, !fabRevealed)
                    fabRevealed = !fabRevealed

                    DrawableHelper.modifyFab(binding.root.context, binding.fabTour,
                            if (fabRevealed) R.drawable.ic_baseline_close_24 else R.drawable.ic_baseline_launch_24
                    )
                }
            }
        }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        private fun <T> animateCircularReveal(circularRevealWidget: T, expand: Boolean = true) where T : View?, T : CircularRevealWidget? {
            circularRevealWidget!!.post {
                val viewWidth = circularRevealWidget.width
                val viewHeight = circularRevealWidget.height
                val viewDiagonal = sqrt((viewWidth * viewWidth + viewHeight * viewHeight).toDouble()).toInt()

                val animatorSet = AnimatorSet().apply {
                    playTogether(
                            CircularRevealCompat.createCircularReveal(
                                    circularRevealWidget,
                                    (viewWidth - 132).toFloat(),
                                    (viewHeight / 2).toFloat() + 50f,
                                    if (expand) 10f else 1000f,
                                    if (expand) (viewDiagonal / 1.2).toFloat() else 10f
                            ),
                            ObjectAnimator.ofArgb(
                                    circularRevealWidget,
                                    CircularRevealWidget.CircularRevealScrimColorProperty.CIRCULAR_REVEAL_SCRIM_COLOR,
                                    ContextCompat.getColor(circularRevealWidget.context, R.color.purple_200),
                                    Color.TRANSPARENT)
                    )

                    duration = 512

                    addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)
                            if (!expand) circularRevealWidget.visibility = View.INVISIBLE
                        }
                    })
                }

                circularRevealWidget.visibility = View.VISIBLE
                animatorSet.start()
            }
        }

        private val TOUR_BRIEF_ITEM_CALLBACK: DiffUtil.ItemCallback<TourRouteBrief> = object : DiffUtil.ItemCallback<TourRouteBrief>() {
            override fun areItemsTheSame(oldItem: TourRouteBrief, newItem: TourRouteBrief): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: TourRouteBrief, newItem: TourRouteBrief): Boolean {
                return oldItem.title == newItem.title &&
                        oldItem.description == newItem.description &&
                        oldItem.image == newItem.image &&
                        oldItem.created_at == newItem.created_at &&
                        oldItem.updated_at == newItem.updated_at
            }
        }
    }
}