package pt.isec.amov.tp1

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import pt.isec.amov.tp1.databinding.ActivitySingleplayerBinding
import android.content.DialogInterface
import android.widget.Toast
import kotlin.math.abs

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

        viewModel.state.observe(this, Observer { newState ->
            when(newState){
                State.PLAYING_GAME -> {
                    viewModel.score.observe(this, Observer { newScore ->
                        updateScoreTextView(newScore)
                    })
                    viewModel.levelNumber.observe(this, Observer { newLevel ->
                        updateLevelTextView(newLevel)
                    })
                    viewModel.correctAnswers.observe(this, Observer { newCorrectAnswersNumber ->
                        updateCorrectAnswersTextView(newCorrectAnswersNumber)
                    })
                    viewModel.wrongAnswers.observe(this, Observer { newWrongAnswersNumber ->
                        updateWrongAnswersTextView(newWrongAnswersNumber)
                    })
                    viewModel.elapsedTime.observe(this, Observer { newTimerValue ->
                        updateTimerTextView(newTimerValue)
                    })
                }
                State.GAME_OVER -> {
                    val builder= AlertDialog.Builder(this)
                    builder.setTitle("Fim do Jogo")
                    builder.setMessage("Recomeçar jogo?")
                    builder.setPositiveButton("Recomeçar",
                        DialogInterface.OnClickListener { dialog, _ ->
                            viewModel.restartGame()
                            dialog.dismiss()
                        })
                    val dialog: AlertDialog = builder.create()
                    dialog.show()
                }
            }
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

    private fun updateCorrectAnswersTextView(newCorrectAnswersNumber: Int?) {
        binding.correctAnswersTv.text = "Correct: $newCorrectAnswersNumber"
    }

    private fun updateWrongAnswersTextView(newWrongAnswersNumber: Int?) {
        binding.wrongAnswersTv.text = "Wrong: $newWrongAnswersNumber"
    }

    private fun updateTimerTextView(newTimerValue: Long?) {
        binding.tvTimer.text = "Time: $newTimerValue"
    }

    fun createGestureDetector(){
        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(
                e1: MotionEvent,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                val isTabChanged: Boolean
                val position = binding.tabGv.pointToPosition(e1.getX().toInt(), e1.getY().toInt());
                Log.d(TAG, "onFling / Position: $position")

                // Calculate the position and direction of the swipe gesture
                val dx = e2.x - e1.x
                val dy = e2.y - e1.y
                if (abs(dx) > abs(dy)) {
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