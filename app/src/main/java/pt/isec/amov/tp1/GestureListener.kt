package pt.isec.amov.tp1

import android.content.Context
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.GridView

class GestureListener (context: Context, private val gridView: GridView): OnTouchListener, SimpleOnGestureListener() {
    val detector: GestureDetector = GestureDetector(context, this)

    companion object{
        private const val TAG = "TAG"
        private const val SWIPE_THRESHOLD = 100;
        private const val SWIPE_VELOCITY_THRESHOLD = 100;
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        return detector.onTouchEvent(event)
    }

    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        val position = gridView.pointToPosition(e1.getX().toInt(), e1.getY().toInt());
        Log.d(TAG, "POSITION:$position")

        val diffY: Float = e2.getY() - e1.getY()
        val diffX: Float = e2.getX() - e1.getX()
        if (Math.abs(diffX) > Math.abs(diffY)) {
            if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffX > 0) {
                    Log.d(TAG, "DIREITA")

                } else {
                    Log.d(TAG, "ESQUERDA")
                }
            }
        } else {
            if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffY > 0) {
                    Log.d(TAG, "BAIXO")
                } else {
                    Log.d(TAG, "CIMA")

                }
            }
        }
        return true
    }
}