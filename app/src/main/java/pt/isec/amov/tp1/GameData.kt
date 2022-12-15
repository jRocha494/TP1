package pt.isec.amov.tp1

import java.util.Collections

class GameData {
    lateinit var tab: ArrayList<String>
    val results  = ArrayList<Double>()

    companion object {
        private const val BOARD_SIZE = 25
        private const val ADDITION = "+"
        private const val SUBTRACTION = "-"
        private const val MULTIPLICATION = "*"
        private const val DIVISION = "/"
        private const val WHITESPACE = ""
        private val BLANK_BOARD_POSITIONS = arrayOf(6, 8, 16, 18)
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
}