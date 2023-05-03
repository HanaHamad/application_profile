package com.example.applicationprofile

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.applicationprofile.databinding.ActivityLoginBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val TOPIC = "/topics/myTopic2"

class loginActivity : AppCompatActivity() {

    private lateinit var et_email_login: TextView
    private lateinit var et_password_login: TextView
    private lateinit var btn_login: Button
    private lateinit var btnSignup: TextView
    private lateinit var binding: ActivityLoginBinding

    private var auth : FirebaseAuth?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        if (auth!!.currentUser !=null){
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }

        setContentView(R.layout.activity_login)
        et_email_login = findViewById(R.id.et_email_login) as EditText
        et_password_login = findViewById(R.id.et_password_login) as EditText
        btnSignup = findViewById(R.id.btn_sign_up)
        btn_login = findViewById(R.id.btn_login) as Button

        btnSignup!!.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this,signupActivity::class.java))
        })

        btn_login!!.setOnClickListener(View.OnClickListener {
            val email = et_email_login!!.text.toString().trim()
            val password = et_password_login!!.text.toString().trim()

            if (TextUtils.isEmpty(email)){
                Toast.makeText(applicationContext,"Please Entre your email.",Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(applicationContext, "Please Enter your Password", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }

            auth!!.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, OnCompleteListener {
                        task ->

                    if (!task.isSuccessful){
                        if (password.length < 6){
                            et_password_login!!.setError(getString(R.string.minimum_password))
                        }else{
                            Toast.makeText(this,getString(R.string.auth_failed),Toast.LENGTH_LONG).show()
                        }
                    }else{
                        startActivity(Intent(this,MainActivity::class.java))

                    }
                })
        })

    }

}