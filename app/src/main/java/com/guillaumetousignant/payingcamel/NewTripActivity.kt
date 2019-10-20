package com.guillaumetousignant.payingcamel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class NewTripActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_trip)

        setSupportActionBar(findViewById(R.id.new_trip_toolbar))
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp) // set drawable icon
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        window.statusBarColor = getColor(R.color.colorPrimaryDark) // Why is this needed??
    }

    companion object {
        const val EXTRA_PATH = "com.example.android.wordlistsql.REPLY_PATH"
        const val EXTRA_FROM = "com.example.android.wordlistsql.REPLY_FROM"
        const val EXTRA_TO = "com.example.android.wordlistsql.REPLY_TO"
        const val EXTRA_DISTANCE = "com.example.android.wordlistsql.REPLY_DISTANCE"
        const val EXTRA_START = "com.example.android.wordlistsql.REPLY_START"
        const val EXTRA_COURSE = "com.example.android.wordlistsql.REPLY_COURSE"
        const val EXTRA_SKATER = "com.example.android.wordlistsql.REPLY_SKATER"
        const val EXTRA_NAME = "com.example.android.wordlistsql.REPLY_NAME"
        const val EXTRA_NOTE = "com.example.android.wordlistsql.REPLY_NOTE"
        const val EXTRA_CALENDAR = "com.example.android.wordlistsql.INPUT_CALENDAR"
    }
}
