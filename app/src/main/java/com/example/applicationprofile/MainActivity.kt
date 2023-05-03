package com.example.applicationprofile

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.applicationprofile.databinding.ActivityMainBinding
import coil.load
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    var imageUrl = ""

    // private var db = Firebase.firestore
    //private var storage :FirebaseStorage?= null
    // private var auth : FirebaseAuth?= null
    private var newImage = ""
    lateinit var resultLauncher: ActivityResultLauncher<Intent>
    lateinit var const: Constans
//    val email = intent.getStringExtra("nameadd").toString()
  //  val pass = intent.getStringExtra("pass").toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //storage = FirebaseStorage.getInstance()
        //db = FirebaseFirestore.getInstance()
        //auth = FirebaseAuth.getInstance()
        const = Constans(this)
       // getUserInfo()
        getUserInfo()
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    // There are no request codes
                    val intent: Intent? = result.data
                    val uri = intent?.data  //The uri with the location of the file
                    val file = const.getFile(this, uri!!)
                    val new_uri = Uri.fromFile(file)

                    val reference = const.storage.child("Images/${new_uri.lastPathSegment}")
                    val uploadTask = reference.putFile(new_uri)

                    uploadTask.addOnFailureListener { e ->
                    }.addOnSuccessListener { taskSnapshot ->
                        taskSnapshot.storage.downloadUrl.addOnSuccessListener {
                            newImage = it.toString()
                            binding.UserImage.load(newImage)
                        }
                    }
                }
            }

        binding.UserImage.setOnClickListener {
            val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT)
            resultLauncher.launch(Intent.createChooser(intent, "Select image"))
        }
        binding.addbtn.setOnClickListener {
            when {
                binding.NameEdit.text.toString().isEmpty() -> {
                    Toast.makeText(this, "name is empty", Toast.LENGTH_SHORT).show()
                }
                newImage.isEmpty() -> {
                    Toast.makeText(this, "image is empty", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    user(binding.NameEdit.text.toString(),binding.PhoneNumberEdit.text.toString())
                }
            }
        }

        binding.update.setOnClickListener {
            updateUserInfo(
                binding.NameEdit.text.toString(), imageUrl , binding.PhoneNumberEdit.text.toString())
        }
    }
    private fun user(name: String,Phone:String) {
        val user = mapOf(
            "name" to name,
            "Image" to newImage,
            "Phone" to Phone,
            "Email" to const.auth.currentUser!!.email
        )
        const.db.collection("Users").add(user).addOnSuccessListener {
            Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show()

            //--------------
            onBackPressed()
            finish()
        }
    }

    private fun updateUserInfo(Name: String, newImage: String ,Phone: String) {
        val email = const.auth.currentUser!!.email
        if (newImage.isNotEmpty()) {
            const.db.collection("Users").document(email.toString())
                .update(
                    "Name", Name,
                    "Image", newImage,
                    "Phone", Phone,
                ).addOnSuccessListener {
                    Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show()
                }
        } else {
            const.db.collection("Users").document(email.toString())
                .update(
                    "Name", Name,
                    "Image", imageUrl,
                    "Phone", Phone,
                ).addOnSuccessListener {
                    Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun getUserInfo() {
        val email = const.auth.currentUser!!.email.toString()
        const.db.collection("Users").get()
            .addOnSuccessListener {
                Log.e("aasss",it.documents[0].get("name").toString())
                binding.NameEdit.setText(it.documents[0].get("name").toString())
                binding.PhoneNumberEdit.setText(it.documents[0].get("Phone").toString())
                binding.emailEdit.setText(it.documents[0].get("Email").toString())
                if (it.documents[0].get("Image").toString().isNotEmpty()) {
                    binding.UserImage.load(it.documents[0].get("Image").toString())
                }
            }.addOnFailureListener {
                Log.e("error message","AndACustomTag")
            }
    }

}