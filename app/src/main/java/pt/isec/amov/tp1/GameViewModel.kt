package pt.isec.amov.tp1

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class GameViewModel() : ViewModel() {
    var tab: ArrayList<String>
    lateinit var results: ArrayList<Double>
    lateinit var biggestResults: ArrayList<Double>

    var score = MutableLiveData<Int>()
    var levelNumber = MutableLiveData<Int>()
    var attempts = MutableLiveData<Int>()

    private var minValue: Int
    private var maxValue: Int
    private var maxOperator: Int

    companion object {
        private const val BOARD_SIZE = 25
        private const val ADDITION = "+"
        private const val SUBTRACTION = "-"
        private const val MULTIPLICATION = "*"
        private const val DIVISION = "/"
        private const val WHITESPACE = ""
        private val BLANK_BOARD_POSITIONS = arrayOf(6, 8, 16, 18)
        private const val TAG = "TAG"
    }

    init {
        score.value = 0
        levelNumber.value = 1
        attempts.value = 0

        minValue = 1
        maxValue = 9
        maxOperator = 0 //Primeiro nivel comeca por ter so adicao
        tab = populateGameTab(this)
    }

    private fun populateGameTab(gameViewModel: GameViewModel): ArrayList<String>{
        gameViewModel.tab = ArrayList()
        gameViewModel.results = ArrayList()

        for(i in 0 until BOARD_SIZE){
            if(BLANK_BOARD_POSITIONS.contains(i))
                gameViewModel.tab.add(i, WHITESPACE)
            else if(i % 2 == 0)
                gameViewModel.tab.add(i, gameViewModel.randomNumberGenerator(minValue, maxValue))
            else
                gameViewModel.tab.add(i, gameViewModel.randomOperatorGenerator(maxOperator))
        }

        val expression = ArrayList<String>()

        //Linhas
        for (i in 0 until BOARD_SIZE step 10){
            for (j in 0 until 5){
                expression.add(gameViewModel.tab[i+j])
            }
            gameViewModel.results.add(gameViewModel.computeExpression(expression))
            expression.clear()
        }

        //Colunas
        for (j in 0 until 5 step 2){
            for (i in 0 until BOARD_SIZE step 5){
                expression.add(gameViewModel.tab[i+j])
            }
            gameViewModel.results.add(gameViewModel.computeExpression(expression))
            expression.clear()
        }

        var aux = ArrayList<Double>()
        aux.addAll(gameViewModel.results)
        aux.sortDescending()
        gameViewModel.biggestResults = ArrayList()
        gameViewModel.biggestResults.add(aux[0])
        gameViewModel.biggestResults.add(aux[1])
        Log.d(TAG, "BIGGEST RESULTS: ${gameViewModel.results.indexOf(aux[0])},${gameViewModel.results.indexOf(aux[1])}") //LOG PARA DEBUG

        return gameViewModel.tab
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
        val rand = randomNumberGenerator(0, maxOperator)

        return when (rand) {
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

        nextLevel(selectedResult)
        return true
    }

    private fun nextLevel(selectedResult: Double){
        if(selectedResult == biggestResults[0]) { //Melhor resultado
            score.value = (score.value)?.plus(2)
        }else if (selectedResult == biggestResults[1]){ //Segundo melhor resultado
            score.value = (score.value)?.plus(1)
        }

        attempts.value = (attempts.value)?.plus(1)
        if(attempts.value == 5) { //Novo nivel ou perde caso sejam atingidas 5 tentativas
            levelNumber.value = (levelNumber.value)?.plus(1)

            if(levelNumber.value!! < 5){
                maxOperator += 1 //Passa a poder incluir mais um operador
            }else{
                if (levelNumber.value!! % 2 != 0)
                    maxValue *= 11 //se o nivel for impar
                else
                    minValue *= 10
            }

            attempts.value = 0
        }

        tab = populateGameTab(this)
    }
}