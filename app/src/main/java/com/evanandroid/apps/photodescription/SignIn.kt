package com.evanandroid.apps.photodescription

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignIn : AppCompatActivity() {
    lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        val intent = Intent(this,Main::class.java)
        if(auth.currentUser != null){
            startActivity(intent)
        }

        signin_bar.setOnClickListener {
            if(id_bar.text.toString().length > 0 && secreat_num.text.toString().length > 0){
                auth.signInWithEmailAndPassword(id_bar.text.toString(), secreat_num.text.toString())
                    .addOnCompleteListener(this) { task ->
                        //회원정보가 있는지 없는지 알아서 구분하는 코드
                        if (task.isSuccessful) {
                            startActivity(intent)
                        } else {
                            Toast.makeText(baseContext, "회원가입부터 진행해주세요",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
            }
            else {
                Toast.makeText(this ,"아이디 혹은 비밀번호를 입력해주세요", Toast.LENGTH_LONG).show()

            }
        }
        go_login.setOnClickListener {
            val upintent = Intent(this, Login::class.java)
            startActivity(upintent)
        }
    }
}