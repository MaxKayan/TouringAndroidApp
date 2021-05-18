package net.inqer.touringapp.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class MarginItemDecoration(
    private val spaceSize: Int,
    private val direction: Direction = Direction.VERTICAL
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        with(outRect) {
            when (direction) {
                Direction.VERTICAL -> {
                    if (parent.getChildAdapterPosition(view) == 0) {
                        top = spaceSize
                    }
                    left = spaceSize
                    right = spaceSize
                    bottom = spaceSize
                }
                Direction.HORIZONTAL -> {
                    if (parent.getChildAdapterPosition(view) != 0) {
                        left = spaceSize
                    }
//                    top = spaceSize
//                    right = spaceSize
//                    bottom = spaceSize
                }
            }

        }
    }

    enum class Direction {
        VERTICAL,
        HORIZONTAL
    }
}