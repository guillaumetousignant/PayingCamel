package com.guillaumetousignant.payingcamel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu

class NewPathActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_path)

        setSupportActionBar(findViewById(R.id.new_path_toolbar))
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp) // set drawable icon
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        window.statusBarColor = getColor(R.color.colorPrimaryDark) // Why is this needed??
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.new_word_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    companion object {
        const val EXTRA_DISTANCE = "com.example.android.wordlistsql.REPLY_DISTANCE"
        const val EXTRA_FROM = "com.example.android.wordlistsql.REPLY_FROM"
        const val EXTRA_TO = "com.example.android.wordlistsql.REPLY_TO"
        const val EXTRA_NAME = "com.example.android.wordlistsql.REPLY_NAME"
        const val EXTRA_NOTE = "com.example.android.wordlistsql.REPLY_NOTE"
    }
}
