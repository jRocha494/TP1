package pt.isec.amov.tp1

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import pt.isec.amov.tp1.databinding.ActivityMainBinding
import java.io.File


class MainActivity : AppCompatActivity() {
    private lateinit var sharedPref: SharedPreferences
    private lateinit var binding: ActivityMainBinding
    private var username: String? = null
    private var imagePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPref = getSharedPreferences("profile", Context.MODE_PRIVATE)

        username = sharedPref.getString("username", "")
        imagePath = sharedPref.getString("imagePath", "")
        if(username.isNullOrEmpty() || imagePath.isNullOrEmpty() ) {
            binding.profileLl.isVisible = false
        }else {
            binding.profileLl.isVisible = true
            binding.profileTv.text = username
            binding.profileIv.setImageURI(Uri.fromFile(File(imagePath)));
        }

        binding.btnSingleplayer.setOnClickListener {
            if(username.isNullOrEmpty() || imagePath.isNullOrEmpty() ) {
                Toast.makeText(this, "${R.string.msg_username_image_empty}", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(this, SingleplayerActivity::class.java)
            startActivity(intent)
        }

        binding.btnMultiplayer.setOnClickListener {
            if(username.isNullOrEmpty() || imagePath.isNullOrEmpty() ) {
                Toast.makeText(this, "${R.string.msg_username_image_empty}", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
//            val intent = Intent(this, MultiplayerActivity::class.java)
//            startActivity(intent)
            Toast.makeText(this, "${R.string.msg_multiplayer_not_implemented}", Toast.LENGTH_SHORT).show()
        }

        binding.btnSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        binding.btnHistory.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        username = sharedPref.getString("username", "")
        imagePath = sharedPref.getString("imagePath", "")
        if(username.isNullOrEmpty() || imagePath.isNullOrEmpty() ) {
            binding.profileLl.isVisible = false
        }else {
            binding.profileLl.isVisible = true
            binding.profileTv.text = username
            binding.profileIv.setImageURI(Uri.fromFile(File(imagePath)));
        }
    }

    override fun onDestroy() {
        val editor = sharedPref.edit()
        editor.clear()
        editor.apply()
        super.onDestroy()
    }
}