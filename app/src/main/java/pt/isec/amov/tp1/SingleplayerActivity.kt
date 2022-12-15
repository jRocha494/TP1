package pt.isec.amov.tp1

import android.os.Bundle
import android.provider.ContactsContract.Data
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import pt.isec.amov.tp1.databinding.ActivitySingleplayerBinding

class SingleplayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySingleplayerBinding
    private val data: GameData = GameData()
    private lateinit var gestureDetector: GestureDetector

    companion object {
        private const val TAG = "TAG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySingleplayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val gridView = binding.tabGv
        val adapter = Adapter(this, R.layout.game_cell_text_view, data.populateGameTab(1,9))
        gridView.adapter = adapter

        createGestureDetector()
        gridView.setOnTouchListener { _, event -> gestureDetector.onTouchEvent(event) }
    }

    fun createGestureDetector(){
        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(
                e1: MotionEvent,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                val position = binding.tabGv.pointToPosition(e1.getX().toInt(), e1.getY().toInt());
                Log.d(TAG, "onFling / Position: $position")

                // Calculate the position and direction of the swipe gesture
                val dx = e2.x - e1.x
                val dy = e2.y - e1.y
                if (Math.abs(dx) > Math.abs(dy)) {
                    // Swipe is horizontal
                    if (dx > 0) {
                        // Swipe is to the right
                        Log.d(TAG,"RIGHT")
                    } else {
                        // Swipe is to the left
                        Log.d(TAG,"LEFT")
                    }
                } else {
                    // Swipe is vertical
                    if (dy > 0) {
                        // Swipe is to the bottom
                        Log.d(TAG,"DOWN")
                    } else {
                        // Swipe is to the top
                        Log.d(TAG,"UP")
                    }
                }
                return true
            }
        })
    }
}