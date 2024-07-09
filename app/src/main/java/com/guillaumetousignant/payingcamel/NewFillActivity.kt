package com.guillaumetousignant.payingcamel

import android.app.Activity
import android.content.Intent
import android.icu.text.NumberFormat
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.guillaumetousignant.payingcamel.ui.gas.NewFillViewModel
import com.guillaumetousignant.payingcamel.ui.gas.NewFillViewModelFactory
import com.guillaumetousignant.payingcamel.ui.pickers.DatePickerFragment
import com.guillaumetousignant.payingcamel.ui.pickers.TimePickerFragment
import java.text.DateFormat

class NewFillActivity : AppCompatActivity(R.layout.activity_new_fill) {

    private lateinit var newFillViewModel: NewFillViewModel // Added
    private lateinit var editNameView: EditText
    private lateinit var startTimeText: TextView
    private lateinit var startDateText: TextView
    private lateinit var editNoteView : EditText
    private lateinit var editAmountView : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        editNameView = findViewById(R.id.fill_edit_name)
        startTimeText = findViewById(R.id.fill_start_time)
        startDateText = findViewById(R.id.fill_start_date)
        editNoteView = findViewById(R.id.fill_edit_note)
        editAmountView = findViewById(R.id.fill_amount)

        val initCalendar = intent.getSerializableExtra(EXTRA_CALENDAR) as Calendar? ?:Calendar.getInstance()
        initCalendar.set(Calendar.MILLISECOND, 0)
        initCalendar.set(Calendar.SECOND, 0)
        initCalendar.set(Calendar.MINUTE, 0)
        val factory = NewFillViewModelFactory(application, initCalendar)
        newFillViewModel =
            ViewModelProvider(this, factory)[NewFillViewModel::class.java] // Added

        setSupportActionBar(findViewById(R.id.new_fill_toolbar))
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp) // set drawable icon
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        window.statusBarColor = getColor(R.color.colorPrimaryDark) // Why is this needed??

        val startObserver = Observer<Calendar> { calendar ->
            // Update the UI, in this case, a TextView.
            val timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT) // CHECK add locale
            val dateFormat = DateFormat.getDateInstance(DateFormat.LONG) // CHECK add locale
            //getTimeInstance

            startTimeText.text = timeFormat.format(calendar.time)
            startDateText.text = dateFormat.format(calendar.time)
        }

        newFillViewModel.startCalendar.observe(this, startObserver)

        editAmountView.addTextChangedListener(object : TextWatcher {
            var current = ""
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString() != current){
                    editAmountView.removeTextChangedListener(this)

                    val replaceable =
                        String.format("[%s,.]", NumberFormat.getCurrencyInstance().currency.symbol)
                    val cleanString = s.toString().replace(replaceable.toRegex(), "").replace("\\s".toRegex(), "")
                    val parsed = cleanString.toDouble()
                    val formatted = NumberFormat.getCurrencyInstance().format((parsed/100))

                    current = formatted
                    editAmountView.setText(formatted)
                    editAmountView.setSelection(formatted.length)

                    editAmountView.addTextChangedListener(this)
                }
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            android.R.id.home -> {
                val replyIntent = Intent()
                setResult(Activity.RESULT_CANCELED, replyIntent)
                finish()
                true
            }
            R.id.new_word_save_button -> {
                if (TextUtils.isEmpty((editAmountView.text))) {
                    val view = findViewById<View>(android.R.id.content)

                    Snackbar.make(view, R.string.empty_amount, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
                }
                else {
                    val replyIntent = Intent()

                    val name = if (TextUtils.isEmpty(editNameView.text)) {
                        null
                    } else {
                        editNameView.text.toString()
                    }
                    val note = if (TextUtils.isEmpty(editNoteView.text)) {
                        null
                    } else {
                        editNoteView.text.toString()
                    }
                    val replaceable =
                        String.format("[%s,.]", NumberFormat.getCurrencyInstance().currency.symbol)
                    val cleanString = editAmountView.text.toString().replace(replaceable.toRegex(), "").replace("\\s".toRegex(), "")

                    replyIntent.putExtra(EXTRA_NAME, name)
                    replyIntent.putExtra(EXTRA_START, newFillViewModel.startCalendar.value)
                    replyIntent.putExtra(EXTRA_AMOUNT, cleanString.toInt())
                    replyIntent.putExtra(EXTRA_NOTE, note)

                    setResult(Activity.RESULT_OK, replyIntent)
                    finish()
                }

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.new_word_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    fun showStartTimePickerDialog(@Suppress("UNUSED_PARAMETER") v: View) {
        TimePickerFragment(newFillViewModel.startCalendar).show(supportFragmentManager, "StartTimePicker")
    }

    fun showStartDatePickerDialog(@Suppress("UNUSED_PARAMETER") v: View) {
        DatePickerFragment(newFillViewModel.startCalendar).show(supportFragmentManager, "StartDatePicker")
    }

    companion object {
        const val EXTRA_NAME = "com.example.android.wordlistsql.REPLY_NAME"
        const val EXTRA_START = "com.example.android.wordlistsql.REPLY_START"
        const val EXTRA_AMOUNT = "com.example.android.wordlistsql.REPLY_AMOUNT"
        const val EXTRA_NOTE = "com.example.android.wordlistsql.REPLY_NOTE"
        const val EXTRA_CALENDAR = "com.example.android.wordlistsql.INPUT_CALENDAR"
    }
}
