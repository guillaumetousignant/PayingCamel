package com.guillaumetousignant.payingcamel

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText

/**
 * Activity for entering a word.
 */
class NewCourseActivity : AppCompatActivity() {

    private lateinit var editCourseView: EditText

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_course)
        editCourseView = findViewById(R.id.edit_course)

        val button = findViewById<Button>(R.id.button_save_course)
        button.setOnClickListener {
            val replyIntent = Intent()
            if (TextUtils.isEmpty(editCourseView.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {
                val course = editCourseView.text.toString()
                replyIntent.putExtra(EXTRA_REPLY, course)
                setResult(Activity.RESULT_OK, replyIntent)
            }
            finish()
        }
    }

    companion object {
        const val EXTRA_REPLY = "com.example.android.wordlistsql.REPLY"
    }
}
