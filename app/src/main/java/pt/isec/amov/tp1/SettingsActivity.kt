package pt.isec.amov.tp1

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import pt.isec.amov.tp1.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var sharedPref: SharedPreferences
    private lateinit var binding: ActivitySettingsBinding
    private var imagePath : String? = null

    companion object{
        val GALLERY_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPref = getSharedPreferences("profile", Context.MODE_PRIVATE)

        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)

        binding.imagePickerButton.setOnClickListener{
            startActivityForResult(intent, GALLERY_REQUEST_CODE)
        }

        binding.loginButton.setOnClickListener{
            if(binding.usernameEditText.text.isNullOrEmpty()) {
                Toast.makeText(this, "${R.string.msg_username_empty}", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            var result = false

            runBlocking{ launch {  result = alreadyExists() } }

            if(result){
                Toast.makeText(this, "${R.string.msg_username_already_exists}", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(imagePath.isNullOrEmpty()) {
                Toast.makeText(this, "${R.string.msg_image_path_empty}", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            runBlocking { addProfile(Profile(binding.usernameEditText.text.toString(), imagePath!!)) }

            val editor = sharedPref.edit()
            editor.putString("username", binding.usernameEditText.text.toString())
            editor.putString("imagePath", imagePath)
            editor.apply()
            finish()
        }
    }

    suspend fun alreadyExists() : Boolean{
        val firestore = FirebaseFirestore.getInstance()
        val gameScoreObjectsRef = firestore.collection("profiles_collection")

        val query = gameScoreObjectsRef.whereEqualTo("username", binding.usernameEditText.text.toString())

        val snapshot = query.get().await()

        return !snapshot.isEmpty
    }

    suspend fun addProfile(newProfile: Profile){
        val firestore = FirebaseFirestore.getInstance()
        val gameScoreObjectsRef = firestore.collection("profiles_collection")
        gameScoreObjectsRef.add(newProfile).await()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == GALLERY_REQUEST_CODE) {
            val pickedImage: Uri? = data?.data
            var path = pickedImage?.let { getPathFromURI(it) }
            if(path != null) {
                imagePath = path
                binding.imagePathTv.text=imagePath
            }
        }
    }

    fun getPathFromURI(uri: Uri): String? {
        val cursor = contentResolver.query(uri, null, null, null, null)
        return cursor?.use {
            it.moveToFirst()
            val idx = it.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            it.getString(idx)
        }
    }
}