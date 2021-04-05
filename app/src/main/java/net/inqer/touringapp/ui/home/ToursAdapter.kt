package net.inqer.touringapp.ui.home

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
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
        return TourViewHolder(this, binding, callbacks, revealedStates)
    }

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int) = getItem(position).id

    private lateinit var recyclerView: RecyclerView

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: TourViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private val revealedStates: HashMap<Long, Boolean> = HashMap()

    private fun updateStatesWithList(list: MutableList<TourRoute>) {
        val diff = revealedStates.toMutableMap()
        list.forEach { route ->
            diff.remove(route.id)
        }

        diff.keys.forEach { removedRouteId -> revealedStates.remove(removedRouteId) }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun closeOthers(id: Long) {
        revealedStates.filter { it.value && it.key != id }.forEach { entry ->
            val holder = recyclerView.findViewHolderForItemId(entry.key) as TourViewHolder?
            Log.d(TAG, "closeOthers: holder - $holder")
//            holder?.performCardReveal(getItem(holder.adapterPosition))

            if (holder != null) {
                holder.performCardReveal(getItem(holder.adapterPosition))
            } else {
                Log.w(TAG, "closeOthers: holder is null!")
                revealedStates[entry.key] = false
                notifyDataSetChanged()
            }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onCurrentListChanged(previousList: MutableList<TourRoute>, currentList: MutableList<TourRoute>) {
        super.onCurrentListChanged(previousList, currentList)
        updateStatesWithList(currentList)
    }

    companion object {
        private const val TAG = "ToursAdapter"

        class TourViewHolder constructor(
                private val adapter: ToursAdapter,
                val binding: ItemTourBinding,
                private val callbacks: OnTourViewInteraction,
                private val states: HashMap<Long, Boolean>
        ) : RecyclerView.ViewHolder(binding.root) {

            private var fabRevealed = false
            private val context = binding.root.context

            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            fun performCardReveal(tour: TourRoute) {
                binding.fabTour.isClickable = false
                animateCircularReveal(context, binding.innerCard, !fabRevealed, object : AnimationCallback {
                    override fun onStart() {
                        fabRevealed = !fabRevealed

                        val revealed = states[tour.id]
                        if (revealed != null) {
                            states[tour.id] = !revealed
                        } else {
                            states[tour.id] = true
                        }

                        DrawableHelper.modifyFab(context, binding.fabTour,
                                if (fabRevealed) R.drawable.ic_baseline_close_24 else R.drawable.ic_baseline_launch_24
                        )
                    }

                    override fun onEnd() {
                        binding.fabTour.isClickable = true
                        if (fabRevealed) callbacks.cardOpened(tour)
                    }
                })
            }

            interface OnTourViewInteraction {
                fun rootClick(item: TourRoute)
                fun cardOpened(item: TourRoute)
                fun fabClick(item: TourRoute)
                fun launchClick(item: TourRoute)
            }

            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            fun bind(tour: TourRoute) {
                binding.title.text = tour.title
                binding.secondaryText.text = tour.createdAt.toString()
                binding.supportingText.text = tour.description

                binding.innerTitle.text = tour.title
//                binding.innerSubtitle.text = tour.createdAt.toString()
                binding.innerSubtitle.text = "id = ${tour.id}"

                // Logic that depends on current card data state (Partial/Full)
                val isFull = tour.totalDistance != null && tour.estimatedDuration != null
                Log.d(TAG, "bind: ${tour.id} ; isFull = $isFull")
                if (isFull) {
                    // The data we have is full
                    binding.innerProgressBar.visibility = View.INVISIBLE
                    binding.innerTourLength.visibility = View.VISIBLE
                    binding.innerWaypoints.visibility = View.VISIBLE
                    binding.innerTime.visibility = View.VISIBLE
                    binding.innerDestinations.visibility = View.VISIBLE
                    binding.innerBtnStart.isClickable = false

                    binding.innerTourLength.text = context.getString(R.string.tour_length, tour.totalDistance)
                    binding.innerWaypoints.text = context.getString(R.string.n_waypoints, tour.waypoints?.size)
                    binding.innerTime.text = context.getString(R.string.estimated_n_minutes, tour.estimatedDuration)
                    binding.innerDestinations.text = context.getString(R.string.n_destinations, tour.destinations?.size)
                    binding.innerBtnStart.setOnClickListener {
                        callbacks.launchClick(tour)
                    }
                } else {
                    // The data we have is currently partial
                    binding.innerProgressBar.visibility = View.VISIBLE
                    binding.innerTourLength.visibility = View.INVISIBLE
                    binding.innerWaypoints.visibility = View.INVISIBLE
                    binding.innerTime.visibility = View.INVISIBLE
                    binding.innerDestinations.visibility = View.INVISIBLE
                    binding.innerBtnStart.isClickable = false
                }

                fabRevealed = states[tour.id] ?: fabRevealed
                DrawableHelper.modifyFab(context, binding.fabTour,
                        if (fabRevealed) R.drawable.ic_baseline_close_24 else R.drawable.ic_baseline_launch_24
                )
                binding.innerCard.visibility = if (fabRevealed) View.VISIBLE else View.INVISIBLE
                binding.innerCard.circularRevealScrimColor = ContextCompat.getColor(context, android.R.color.transparent)

                val progress = CircularProgressDrawable(context)
                progress.centerRadius = 30f
                progress.strokeWidth = 4f
                progress.setColorSchemeColors(R.color.teal_200, R.color.purple_200)
                progress.start()

                Glide.with(binding.root)
                        .load(tour.image)
                        .placeholder(progress)
                        .into(binding.image)

                Glide.with(binding.root)
                        .load(tour.image)
                        .placeholder(progress)
                        .into(binding.innerImage)

                binding.root.setOnClickListener {
                    callbacks.rootClick(tour)
                }

                binding.fabTour.setOnClickListener {
                    adapter.closeOthers(tour.id)
                    callbacks.fabClick(tour)
                    performCardReveal(tour)
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
//                if (oldItem.id != newItem.id) return false
                if (oldItem.title != newItem.title) return false
                if (oldItem.description != newItem.description) return false
                if (oldItem.image != newItem.image) return false
                if (oldItem.createdAt != newItem.createdAt) return false
                if (oldItem.updatedAt != newItem.updatedAt) return false
                if (oldItem.totalDistance != newItem.totalDistance) return false
                if (oldItem.estimatedDuration != newItem.estimatedDuration) return false
                if (oldItem.waypoints != null) {
                    if (newItem.waypoints == null) return false
                    if (!oldItem.waypoints.contentEquals(newItem.waypoints)) return false
                } else if (newItem.waypoints != null) return false
                if (oldItem.destinations != null) {
                    if (newItem.destinations == null) return false
                    if (!oldItem.destinations.contentEquals(newItem.destinations)) return false
                } else if (newItem.destinations != null) return false

                return true
            }
        }
    }
}