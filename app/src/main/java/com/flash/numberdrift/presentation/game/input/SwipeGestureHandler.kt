package com.flash.numberdrift.presentation.game.input

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import com.flash.numberdrift.domain.model.Direction
import kotlin.math.abs


class SwipeGestureHandler(
    context: Context,
    private val onSwipe: (Direction) -> Unit
) {

    val detector: GestureDetector = GestureDetector(
        context,
        object : GestureDetector.SimpleOnGestureListener() {

            private val SWIPE_THRESHOLD = 100
            private val SWIPE_VELOCITY_THRESHOLD = 100

            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {

                val diffX = e2.x - (e1?.x ?: 0f)
                val diffY = e2.y - (e1?.y ?: 0f)

                if (abs(diffX) > abs(diffY)) {
                    if (abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipe(Direction.RIGHT)
                        } else {
                            onSwipe(Direction.LEFT)
                        }
                        return true
                    }
                } else {
                    if (abs(diffY) > SWIPE_THRESHOLD && abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY > 0) {
                            onSwipe(Direction.DOWN)
                        } else {
                            onSwipe(Direction.UP)
                        }
                        return true
                    }
                }

                return false
            }
        }
    )
}