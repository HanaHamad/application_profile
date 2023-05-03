package com.example.applicationprofile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import com.example.applicationprofile.databinding.ActivitySignupBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class signupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private var inputEmail: EditText? = null
    private var inputPassword: EditText? = null
    private var btnSignIn: TextView? = null
    private var btnSignUp: Button? = null

    private var auth : FirebaseAuth?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        btnSignIn = findViewById(R.id.tv_login)
        btnSignUp = findViewById(R.id.register_button) as Button
        inputEmail = findViewById(R.id.signup_email) as EditText
        inputPassword = findViewById(R.id.signup_password) as EditText


        btnSignIn!!.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, loginActivity::class.java))
            finish()
        })
        btnSignUp!!.setOnClickListener(View.OnClickListener {
            val email = inputEmail!!.text.toString().trim()
            val password = inputPassword!!.text.toString().trim()

            if (TextUtils.isEmpty(email)){
                Toast.makeText(applicationContext,"Enter your email Address!!", Toast.LENGTH_LONG).show()
                return@OnClickListener
            }
            if (TextUtils.isEmpty(password)){
                Toast.makeText(applicationContext,"Enter your Password",Toast.LENGTH_LONG).show()
                return@OnClickListener
            }
            if (password.length < 6){
                Toast.makeText(applicationContext,"Password too short, enter mimimum 6 charcters" , Toast.LENGTH_LONG).show()
                return@OnClickListener
            }

            //create user
            auth!!.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, OnCompleteListener {
                        task ->
                    Toast.makeText(this,"createUserWithEmail:onComplete"+task.isSuccessful,Toast.LENGTH_SHORT).show()

                    if (!task.isSuccessful){
                        Toast.makeText(this,"User Not crated",Toast.LENGTH_SHORT).show()
                        return@OnCompleteListener
                    }else{

                        val i = Intent(this, MainActivity::class.java)
                        i.putExtra("nameadd", email)
                        i.putExtra("pass", password)
                        startActivity(i)
                        finish()
                    }


                })

        })
    }
}
