package com.evanandroid.apps.photodescription

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*

class Login : AppCompatActivity() {
    var auth : FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        FirebaseApp.initializeApp(baseContext)
        auth = FirebaseAuth.getInstance()
        loginBut.setOnClickListener {
            signinAndSignup()
        }
    }
    fun signinAndSignup()
    {
        auth?.createUserWithEmailAndPassword(email.text.toString(),classifiednum.text.toString())?.addOnCompleteListener {
            task ->
                if(task.isSuccessful){
                    //가입에 성공
                    movePage(task.result?.user)
                }else if(task.exception?.message.isNullOrEmpty()){
                    //가입 절차에 맞지 않는 경우
                    Toast.makeText(this,"에러가 발생했습니다. 다시 시도해주시기 바랍니다.",Toast.LENGTH_LONG).show()
                }else{
                    //이미 가입이 되어 있는 경우
                    signinEmail()
                }
        }
    }
    fun signinEmail(){
        auth?.signInWithEmailAndPassword(email.text.toString(),classifiednum.text.toString())?.addOnCompleteListener {
                task ->
            if(task.isSuccessful){
                //login
            }else if(task.exception?.message.isNullOrEmpty()){

            }else{

            }
        }
    }
    fun movePage(user:FirebaseUser?){
        if (user != null){
            startActivity(Intent(this,Main::class.java))
        }
    }
}