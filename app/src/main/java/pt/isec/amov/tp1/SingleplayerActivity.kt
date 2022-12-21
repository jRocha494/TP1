package pt.isec.amov.tp1

import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import pt.isec.amov.tp1.databinding.ActivitySingleplayerBinding

class SingleplayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySingleplayerBinding
    private lateinit var viewModel: GameViewModel
    private lateinit var gestureDetector: GestureDetector
    private lateinit var adapter: Adapter

    companion object {
        private const val TAG = "TAG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySingleplayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(GameViewModel::class.java)
        adapter = Adapter(this, R.layout.game_cell_text_view, viewModel.tab)
        viewModel.score.observe(this, Observer { newScore ->
            updateScoreTextView(newScore)
        })
        viewModel.levelNumber.observe(this, Observer { newLevel ->
            updateLevelTextView(newLevel)
        })
        viewModel.attempts.observe(this, Observer { newAttemptsNumber ->
            updateAttemptsTextView(newAttemptsNumber)
        })

        binding.tabGv.adapter = adapter
        createGestureDetector()
        binding.tabGv.setOnTouchListener { _, event -> gestureDetector.onTouchEvent(event) }
    }

    private fun updateScoreTextView(newScore: Int?) {
        binding.scoreTv.text = "Score: $newScore"
    }

    private fun updateLevelTextView(newLevel: Int?) {
        binding.levelTv.text = "Level: $newLevel"
    }

    private fun updateAttemptsTextView(newAttemptsNumber: Int?) {
        binding.attemptsTv.text = "Attempts: $newAttemptsNumber/5"
    }

    fun createGestureDetector(){
        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(
                e1: MotionEvent,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                var isTabChanged = false
                val position = binding.tabGv.pointToPosition(e1.getX().toInt(), e1.getY().toInt());
                Log.d(TAG, "onFling / Position: $position")

                // Calculate the position and direction of the swipe gesture
                val dx = e2.x - e1.x
                val dy = e2.y - e1.y
                if (Math.abs(dx) > Math.abs(dy)) {
                    // Swipe is horizontal
                    isTabChanged = viewModel.selectExpression(position, true)
                } else {
                    // Swipe is vertical
                    isTabChanged = viewModel.selectExpression(position, false)
                }
                if(isTabChanged) {
                    adapter.clear()
                    adapter.addAll(viewModel.tab)
                    adapter.notifyDataSetChanged()
                }
                return true
            }
        })
    }
}