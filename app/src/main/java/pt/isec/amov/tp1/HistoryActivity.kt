package pt.isec.amov.tp1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import pt.isec.amov.tp1.databinding.ActivityHistoryBinding

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        runBlocking { getFirestoreData() }
    }

    private suspend fun getFirestoreData() {
        var count = 1
        val firestore = FirebaseFirestore.getInstance()
        val gameScoreObjectsRef = firestore.collection("top5_collection")

        val topFiveQuery = gameScoreObjectsRef.orderBy("score", Query.Direction.DESCENDING).limit(5)

        val topList = topFiveQuery.get().await().toObjects(GameScoreObject::class.java)

        for(entry in topList){
            val textViewId = resources.getIdentifier("text_$count", "id", packageName)
            val textView = binding.root.findViewById<TextView>(textViewId)

            textView.text = "$count. User: ${entry.username}\n   Score: ${entry.score}\n   Time: ${entry.gameTime}s"
            count++;
        }
    }
}