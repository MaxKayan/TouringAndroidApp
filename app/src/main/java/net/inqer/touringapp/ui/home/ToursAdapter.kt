package net.inqer.touringapp.ui.home

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
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
import net.inqer.touringapp.data.models.TourRoute
import net.inqer.touringapp.databinding.ItemTourBinding
import net.inqer.touringapp.util.DrawableHelper
import kotlin.math.sqrt


class ToursAdapter constructor(
        private val callbacks: TourViewHolder.OnTourViewInteraction
) : ListAdapter<TourRoute, ToursAdapter.Companion.TourViewHolder>(TOUR_BRIEF_ITEM_CALLBACK) {
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

            private var fabRevealed = false

            interface OnTourViewInteraction {
                fun click(item: TourRoute)
            }

            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            fun bind(tour: TourRoute, callbacks: OnTourViewInteraction) {
                binding.title.text = tour.title
                binding.secondaryText.text = tour.createdAt.toString()
                binding.supportingText.text = tour.description

                binding.innerTitle.text = tour.title
                binding.innerSubtitle.text = tour.createdAt.toString()

                Glide.with(binding.root)
                        .load(tour.image)
                        .into(binding.image)

                Glide.with(binding.root)
                        .load(tour.image)
                        .into(binding.innerImage)

                binding.root.setOnClickListener {
                    callbacks.click(tour)
                }

                binding.fabTour.setOnClickListener {
                    animateCircularReveal(binding.root.context, binding.innerCard, !fabRevealed, object : AnimationCallback {
                        override fun onStart() {
                            it.isClickable = false
                            fabRevealed = !fabRevealed

                            DrawableHelper.modifyFab(binding.root.context, binding.fabTour,
                                    if (fabRevealed) R.drawable.ic_baseline_close_24 else R.drawable.ic_baseline_launch_24
                            )
                        }

                        override fun onEnd() {
                            it.isClickable = true
                        }
                    })
                }
            }
        }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        private fun <T> animateCircularReveal(context: Context, circularRevealWidget: T, expand: Boolean = true, callbacks: AnimationCallback) where T : View?, T : CircularRevealWidget? {
            val primaryColor = ContextCompat.getColor(context, R.color.purple_200)

            circularRevealWidget?.post {
                val viewWidth = circularRevealWidget.width
                val viewHeight = circularRevealWidget.height
                val viewDiagonal = sqrt((viewWidth * viewWidth + viewHeight * viewHeight).toDouble()).toInt()

                val objectAnimator =
                        if (expand)
                            ObjectAnimator.ofArgb(
                                    circularRevealWidget,
                                    CircularRevealWidget.CircularRevealScrimColorProperty.CIRCULAR_REVEAL_SCRIM_COLOR,
                                    primaryColor,
                                    Color.TRANSPARENT)
                        else
                            ObjectAnimator.ofArgb(
                                    circularRevealWidget,
                                    CircularRevealWidget.CircularRevealScrimColorProperty.CIRCULAR_REVEAL_SCRIM_COLOR,
                                    Color.TRANSPARENT,
                                    primaryColor)

                val animatorSet = AnimatorSet().apply {
                    playTogether(
                            CircularRevealCompat.createCircularReveal(
                                    circularRevealWidget,
                                    (viewWidth - 120).toFloat(),
                                    (viewHeight / 2).toFloat() + 42f,
                                    if (expand) 10f else 1000f,
                                    if (expand) (viewDiagonal / 1.2).toFloat() else 10f
                            ),
                            objectAnimator
                    )

                    duration = 512

                    addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationStart(animation: Animator?, isReverse: Boolean) {
                            super.onAnimationStart(animation, isReverse)
                            callbacks.onStart()
                        }

                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)
                            if (!expand) circularRevealWidget.visibility = View.INVISIBLE
                            callbacks.onEnd()
                        }
                    })
                }

                if (expand) circularRevealWidget.visibility = View.VISIBLE
                animatorSet.start()
            }
        }

        private interface AnimationCallback {
            fun onStart()
            fun onEnd()
        }

        private val TOUR_BRIEF_ITEM_CALLBACK: DiffUtil.ItemCallback<TourRoute> = object : DiffUtil.ItemCallback<TourRoute>() {
            override fun areItemsTheSame(oldItem: TourRoute, newItem: TourRoute): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: TourRoute, newItem: TourRoute): Boolean {
                return oldItem.title == newItem.title &&
                        oldItem.description == newItem.description &&
                        oldItem.image == newItem.image &&
                        oldItem.createdAt == newItem.createdAt &&
                        oldItem.updatedAt == newItem.updatedAt
            }
        }
    }
}