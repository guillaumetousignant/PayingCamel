package com.guillaumetousignant.payingcamel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class NewPathActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_path)
    }

    companion object {
        const val EXTRA_DISTANCE = "com.example.android.wordlistsql.REPLY_DISTANCE"
        const val EXTRA_FROM = "com.example.android.wordlistsql.REPLY_FROM"
        const val EXTRA_TO = "com.example.android.wordlistsql.REPLY_TO"
        const val EXTRA_NAME = "com.example.android.wordlistsql.REPLY_NAME"
        const val EXTRA_NOTE = "com.example.android.wordlistsql.REPLY_NOTE"
    }
}
