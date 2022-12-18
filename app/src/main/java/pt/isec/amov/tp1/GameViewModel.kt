package pt.isec.amov.tp1

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class GameViewModel() : ViewModel() {
    var tab: ArrayList<String>
    val results  = ArrayList<Double>()
    lateinit var biggestResults: HashMap<Int, Double>

    var score = MutableLiveData<Int>()
    var levelNumber = MutableLiveData<Int>()
    var attempts = MutableLiveData<Int>()

    companion object {
        private const val BOARD_SIZE = 25
        private const val ADDITION = "+"
        private const val SUBTRACTION = "-"
        private const val MULTIPLICATION = "*"
        private const val DIVISION = "/"
        private const val WHITESPACE = ""
        private val BLANK_BOARD_POSITIONS = arrayOf(6, 8, 16, 18)
    }

    init {
        tab = populateGameTab(0, 9)
        score.value = 0
        levelNumber.value = 1
    }

    fun populateGameTab(min: Int, max: Int): ArrayList<String>{
        tab = ArrayList()

        for(i in 0 until BOARD_SIZE){
            if(BLANK_BOARD_POSITIONS.contains(i))
                tab.add(i, WHITESPACE)
            else if(i % 2 == 0)
                tab.add(i, randomNumberGenerator(min, max))
            else
                tab.add(i, randomOperatorGenerator())
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

        var aux = results
        aux.sort()
        biggestResults = HashMap()
        biggestResults.put(results.indexOf(aux[0]), aux[0])
        biggestResults.put(results.indexOf(aux[1]), aux[1])

        return tab
    }

    private fun computeExpression(expression : ArrayList<String>) : Double{
        var result : Double = 0.0

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

    private fun randomOperatorGenerator(): String {
        val rand = randomNumberGenerator(0, 3)

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
            val row = (position / 10) % 10 //para verificar se está na primeira fila (posicoes 0 a 4), segunda (10 a 14) ou terceira (20 a 24)

            selectedResult = results[row]
        }
        else{
            val column = position % 10 //para verificar em que coluna está (0 - primeira coluna, 2 - segunda coluna, 4 - terceira coluna)

            if(column % 2 != 0) // a coluna não pode ser 1 ou 3, pois estas nao tem expressões
                return false

            when(column) {
                0 -> selectedResult = results[4] //resultado da primeira coluna no array results
                2 -> selectedResult = results[5] //resultado da segunda coluna no array results
                4 -> selectedResult = results[6] //resultado da terceira coluna no array results
            }
        }

        if(selectedResult == biggestResults.get(0)) { //Melhor resultado
            score.value = (score.value)?.plus(2)
        }else if (selectedResult == biggestResults.get(1)){ //Segundo melhor resultado
            score.value = (score.value)?.plus(1)
        }

        attempts.value = (attempts.value)?.plus(1)
        if(attempts.value == 5) { //Novo nivel ou perde caso sejam atingidas 5 tentativas
            levelNumber.value = (levelNumber.value)?.plus(1)
            attempts.value = 0
        }

        tab = populateGameTab(0 , 9)
        return true
    }
}