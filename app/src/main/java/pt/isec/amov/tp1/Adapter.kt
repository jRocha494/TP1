package pt.isec.amov.tp1

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat

class Adapter(
    context: Context,
    resource: Int,
    private val gameTab: List<String>
) : ArrayAdapter<String>(context,resource,gameTab){

    companion object{
        private val BLANK_BOARD_POSITIONS = arrayOf(6, 8, 16, 18)
    }

    override fun getItem(position: Int): String? {
        return gameTab[position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = super.getView(position, convertView, parent)
        val textView: TextView = view.findViewById(R.id.game_cell_tv)
        textView.text = getItem(position)

        if(BLANK_BOARD_POSITIONS.contains(position)){
            textView.setBackgroundColor(ContextCompat.getColor(context, R.color.board_blank_cell))
        }
        else if(position % 2 == 0){
            textView.setBackgroundColor(ContextCompat.getColor(context, R.color.board_number_cell))
        }
        else{
            textView.setBackgroundColor(ContextCompat.getColor(context, R.color.board_operator_cell))
        }

        return view
    }
}