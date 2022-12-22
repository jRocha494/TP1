package pt.isec.amov.tp1

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*
import kotlin.collections.ArrayList

enum class State {
    PLAYING_GAME, GAME_OVER
}

class GameViewModel() : ViewModel() {
    var tab: ArrayList<String>
    lateinit var results: ArrayList<Double>
    lateinit var biggestResults: ArrayList<Double>

    var score = MutableLiveData<Int>()
    var levelNumber = MutableLiveData<Int>()
    var correctAnswers = MutableLiveData<Int>()
    var wrongAnswers = MutableLiveData<Int>()
    var elapsedTime = MutableLiveData<Long>()
    var state = MutableLiveData<State>()

    private var minValue: Int
    private var maxValue: Int
    private var maxOperator: Int
    private var levelTimeDecrement: Int

    private lateinit var timer: CountDownTimer

    companion object {
        private const val TAG = "TAG"
        private const val BOARD_SIZE = 25
        private const val ADDITION = "+"
        private const val SUBTRACTION = "-"
        private const val MULTIPLICATION = "*"
        private const val DIVISION = "/"
        private const val WHITESPACE = ""
        private val BLANK_BOARD_POSITIONS = arrayOf(6, 8, 16, 18)
        private const val NECESSARY_RIGHT_ANSWERS = 3
        private const val GAME_TIME = 60
        private const val BONUS_TIME = 5
    }

    init {
        score.value = 0
        levelNumber.value = 1
        correctAnswers.value = 0
        wrongAnswers.value = 0
        elapsedTime.value = GAME_TIME.toLong()

        minValue = 1
        maxValue = 9
        maxOperator = 0 //Primeiro nivel comeca por ter so adicao
        levelTimeDecrement = 0

        tab = populateGameTab()
        startTimer()
    }

    private fun initGame(){
        score.value = 0
        levelNumber.value = 1
        correctAnswers.value = 0
        wrongAnswers.value = 0
        elapsedTime.value = GAME_TIME.toLong()

        minValue = 1
        maxValue = 9
        maxOperator = 0 //Primeiro nivel comeca por ter so adicao
        levelTimeDecrement = 0

        tab = populateGameTab()
        startTimer()
    }

    private fun populateGameTab(): ArrayList<String>{
        tab = ArrayList()
        results = ArrayList()

        for(i in 0 until BOARD_SIZE){
            if(BLANK_BOARD_POSITIONS.contains(i))
                tab.add(i, WHITESPACE)
            else if(i % 2 == 0)
                tab.add(i, randomNumberGenerator(minValue, maxValue))
            else
                tab.add(i, randomOperatorGenerator(maxOperator))
        }

        val expression = ArrayList<String>()

        //Linhas
        for (i in 0 until BOARD_SIZE step 10){
            for (j in 0 until 5){
                expression.add(tab[i+j])
            }
            results.add(computeExpression(expression))
            expression.clear()
        }

        //Colunas
        for (j in 0 until 5 step 2){
            for (i in 0 until BOARD_SIZE step 5){
                expression.add(tab[i+j])
            }
            results.add(computeExpression(expression))
            expression.clear()
        }

        var aux = ArrayList<Double>()
        aux.addAll(results)
        aux.sortDescending()
        biggestResults = ArrayList()
        biggestResults.add(aux[0])
        biggestResults.add(aux[1])
        Log.d(TAG, "BIGGEST RESULTS: ${results.indexOf(aux[0])},${results.indexOf(aux[1])}") //LOG PARA DEBUG

        return tab
    }

    private fun computeExpression(expression : ArrayList<String>) : Double{
        var result = 0.0

        //verifica a ordem das operaçoes
        if (expression[1] == ADDITION || expression[1] == SUBTRACTION){
            if (expression[3] == MULTIPLICATION || expression[3] == DIVISION){
                //troca os operadores
                Collections.swap(expression, 1, 3)

                //troca os numeros
                Collections.swap(expression, 0, 2)
                Collections.swap(expression, 2, 4)
            }
        }

        when(expression[1]){
            ADDITION -> result = expression[0].toDouble() + expression [2].toDouble()
            SUBTRACTION -> result = expression[0].toDouble() - expression [2].toDouble()
            MULTIPLICATION -> result = expression[0].toDouble() * expression [2].toDouble()
            DIVISION -> result = expression[0].toDouble() / expression [2].toDouble()
        }

        when(expression[3]){
            ADDITION -> result += expression[4].toDouble()
            SUBTRACTION -> result -= expression[4].toDouble()
            MULTIPLICATION -> result *= expression[4].toDouble()
            DIVISION -> result /= expression[4].toDouble()
        }

        return result
    }

    private fun randomNumberGenerator(min: Int, max: Int): String {
        return (min..max).random().toString()
    }

    private fun randomOperatorGenerator(max: Int): String {
        return when (randomNumberGenerator(0, max)) {
            "0" -> ADDITION
            "1" -> SUBTRACTION
            "2" -> MULTIPLICATION
            "3" -> DIVISION

            else -> {
                WHITESPACE
            }
        }
    }

    fun selectExpression(position: Int, isHorizontal: Boolean) : Boolean{
        var selectedResult = 0.0

        if (isHorizontal){
            when(position) {
                0, 1, 2, 3, 4 -> selectedResult = results[0] //resultado da primeira fila no array results
                10, 11, 12, 13, 14 -> selectedResult = results[1] //resultado da segunda fila no array results
                20, 21, 22, 23, 24 -> selectedResult = results[2] //resultado da terceira fila no array results
                5, 6, 7, 8, 9, 15, 16, 17, 18, 19 -> return false
            }
        }
        else{
            when(position) {
                0, 5, 10, 15, 20 -> selectedResult = results[3] //resultado da terc coluna no array results
                2, 7, 12, 17, 22 -> selectedResult = results[4] //resultado da segunda coluna no array results
                4, 9, 14, 19, 24 -> selectedResult = results[5] //resultado da terceira coluna no array results
                1, 6, 11, 16, 21, 3, 8, 13, 18, 23 -> return false
            }
        }

        processSelection(selectedResult)
        return true
    }

    private fun processSelection(selectedResult: Double){
        if(selectedResult == biggestResults[0]) { //Melhor resultado
            score.value = (score.value)?.plus(2)
            correctAnswers.value = (correctAnswers.value)?.plus(1)
            incrementTimer()
        }else if (selectedResult == biggestResults[1]){ //Segundo melhor resultado
            score.value = (score.value)?.plus(1)
            correctAnswers.value = (correctAnswers.value)?.plus(1)
            incrementTimer()
        }else{
            wrongAnswers.value = (wrongAnswers.value)?.plus(1)
        }

        if((correctAnswers.value!!) == NECESSARY_RIGHT_ANSWERS) { //Novo nivel
            levelNumber.value = (levelNumber.value)?.plus(1)

            if(levelNumber.value!! < 5){
                maxOperator++ //Passa a poder incluir mais um operador
            }else{
                if (levelNumber.value!! % 2 != 0) {
                    maxValue *= 11 //se o nivel for impar
                }
                else
                    minValue *= 10
            }

            levelTimeDecrement++
            correctAnswers.value = 0
            wrongAnswers.value = 0
        }

        tab = populateGameTab()
    }

    private fun startTimer() {
        state.value = State.PLAYING_GAME

        timer = object : CountDownTimer(((elapsedTime.value!!) * 1000), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                elapsedTime.value = millisUntilFinished / 1000
            }

            override fun onFinish() {
                state.value = State.GAME_OVER
            }
        }
        timer.start()
    }

    private fun pauseTimer() {
        timer.cancel()
    }

    private fun incrementTimer(){
        timer.cancel()
        if(elapsedTime.value!! + BONUS_TIME > GAME_TIME - levelTimeDecrement) {
            elapsedTime.value = GAME_TIME.toLong() - levelTimeDecrement
        }else
            elapsedTime.value = elapsedTime.value!! + BONUS_TIME


        startTimer()
    }

    fun restartGame(){
        initGame()
    }
}