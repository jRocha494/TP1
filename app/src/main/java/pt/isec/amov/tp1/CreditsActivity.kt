package pt.isec.amov.tp1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import pt.isec.amov.tp1.databinding.ActivityCreditsBinding
import pt.isec.amov.tp1.databinding.ActivityHistoryBinding

class CreditsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreditsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreditsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}