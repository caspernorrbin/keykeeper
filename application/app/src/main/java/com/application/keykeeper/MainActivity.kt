package com.application.keykeeper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText

class MainActivity : AppCompatActivity() {
    private lateinit var viewOfLayout: View
    private lateinit var editText1: EditText
    private lateinit var editText2: EditText
    private lateinit var button: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        editText1 = findViewById(R.id.et_username)
        editText2 = findViewById(R.id.et_password)
        button = findViewById(R.id.btn_login)




    }
}