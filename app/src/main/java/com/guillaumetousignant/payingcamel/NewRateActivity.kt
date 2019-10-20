package com.guillaumetousignant.payingcamel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class NewRateActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_rate)

        setSupportActionBar(findViewById(R.id.new_rate_toolbar))
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp) // set drawable icon
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        window.statusBarColor = getColor(R.color.colorPrimaryDark) // Why is this needed??
    }

    companion object {
        const val EXTRA_AMOUNT = "com.example.android.wordlistsql.REPLY_AMOUNT"
        const val EXTRA_NAME = "com.example.android.wordlistsql.REPLY_NAME"
        const val EXTRA_NOTE = "com.example.android.wordlistsql.REPLY_NOTE"
        const val EXTRA_SKATER = "com.example.android.wordlistsql.REPLY_SKATER"
    }
}
