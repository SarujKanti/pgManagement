package com.skd.pgmanagement.utils

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.skd.pgmanagement.R
import kotlin.math.sin

abstract class SwipeToDeleteCallback(context: Context)
    : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

    private val backgroundPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.birthDayTextColor)
        isAntiAlias = true
    }

    private val cornerRadius = 20f

    private val deleteIcon: Drawable? =
        ContextCompat.getDrawable(context, R.drawable.ic_delete)

    private val intrinsicWidth = deleteIcon?.intrinsicWidth ?: 0
    private val intrinsicHeight = deleteIcon?.intrinsicHeight ?: 0

    private val textPaint = Paint().apply {
        color = Color.WHITE
        textSize = 42f
        typeface = Typeface.DEFAULT_BOLD
        isAntiAlias = true
    }

    private val deleteText = "Delete"

    private val amplitude = 12f
    private val speed = 2f
    private var animatedValue = 0f

    private val animator = ValueAnimator.ofFloat(0f, (Math.PI * 2).toFloat()).apply {
        duration = 1200
        interpolator = LinearInterpolator()
        repeatCount = ValueAnimator.INFINITE
        addUpdateListener {
            this@SwipeToDeleteCallback.animatedValue = it.animatedValue as Float
        }
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ) = false

    override fun onChildDraw(
        canvas: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {

        val itemView = viewHolder.itemView
        val itemHeight = itemView.height

        val left = itemView.right + dX
        val rect = RectF(left, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat())
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, backgroundPaint)

        deleteIcon?.let { icon ->
            val bounceOffset = sin(animatedValue * speed) * amplitude

            val iconTop = (itemView.top + (itemHeight - intrinsicHeight) / 2 + bounceOffset).toInt()
            val iconBottom = iconTop + intrinsicHeight

            val margin = (itemHeight - intrinsicHeight) / 2
            val iconLeft = itemView.right - margin - intrinsicWidth
            val iconRight = itemView.right - margin

            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
            icon.draw(canvas)

            val textX = iconLeft - 25f - textPaint.measureText(deleteText)
            val textY = itemView.top + itemHeight / 2f + textPaint.textSize / 3
            canvas.drawText(deleteText, textX, textY, textPaint)
        }

        if (isCurrentlyActive && !animator.isStarted) {
            animator.start()
        }

        if (!isCurrentlyActive && animator.isStarted) {
            animator.cancel()
        }

        if (animator.isRunning) {
            recyclerView.postInvalidateOnAnimation()
        }

        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}
