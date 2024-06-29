package com.guillaumetousignant.payingcamel

import android.app.Activity
import android.content.Intent
import android.icu.text.NumberFormat
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.text.TextUtils
import android.widget.EditText

import android.icu.util.Calendar
import android.text.Editable
import android.text.TextWatcher
import com.guillaumetousignant.payingcamel.ui.new_course.NewCourseViewModel
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.guillaumetousignant.payingcamel.ui.pickers.DatePickerFragment
import com.guillaumetousignant.payingcamel.ui.pickers.SkaterPickerFragment
import com.guillaumetousignant.payingcamel.ui.pickers.TimePickerFragment
import java.text.DateFormat
import com.google.android.material.snackbar.Snackbar
import com.guillaumetousignant.payingcamel.database.rate.Rate
import com.guillaumetousignant.payingcamel.database.skater.Skater
import com.guillaumetousignant.payingcamel.ui.pickers.RatePickerFragment
import com.guillaumetousignant.payingcamel.ui.new_course.NewCourseViewModelFactory

/**
 * Activity for entering a word.
 */
class NewCourseActivity : AppCompatActivity(R.layout.activity_new_course) {

    private lateinit var newCourseViewModel: NewCourseViewModel // Added
    private lateinit var editCourseView: EditText
    private lateinit var startTimeText: TextView
    private lateinit var startDateText: TextView
    private lateinit var endTimeText: TextView
    private lateinit var endDateText: TextView
    private lateinit var skaterNameText: TextView
    private lateinit var rateNameText: TextView
    private lateinit var paidCheckbox : CheckBox
    private lateinit var editNoteView : EditText
    private lateinit var amountView : EditText

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        editCourseView = findViewById(R.id.edit_course)
        startTimeText = findViewById(R.id.start_time)
        startDateText = findViewById(R.id.start_date)
        endTimeText = findViewById(R.id.end_time)
        endDateText = findViewById(R.id.end_date)
        skaterNameText = findViewById(R.id.skater_name)
        rateNameText = findViewById(R.id.rate_name)
        paidCheckbox = findViewById(R.id.paid_checkbox)
        editNoteView = findViewById(R.id.edit_note)
        amountView = findViewById(R.id.amount_number)

        val initCalendar = intent.getSerializableExtra(EXTRA_CALENDAR, Calendar::class.java)?:Calendar.getInstance()
        initCalendar.set(Calendar.MILLISECOND, 0)
        initCalendar.set(Calendar.SECOND, 0)
        initCalendar.set(Calendar.MINUTE, 0)
        val factory = NewCourseViewModelFactory(application, initCalendar)
        newCourseViewModel =
            ViewModelProvider(this, factory).get(NewCourseViewModel::class.java) // Added

        setSupportActionBar(findViewById(R.id.new_course_toolbar))
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

            if (!newCourseViewModel.manualAmount) {
                newCourseViewModel.rate.value?.let {
                    val startCalendar =
                        newCourseViewModel.startCalendar.value ?: Calendar.getInstance()
                    val endCalendar = newCourseViewModel.endCalendar.value ?: Calendar.getInstance()
                    //newCourseViewModel.amount.postValue(((endCalendar.timeInMillis - startCalendar.timeInMillis).toDouble() / 3600000 * it.amount).toInt())
                    var timeDifference = endCalendar.timeInMillis - startCalendar.timeInMillis
                    val hours = timeDifference/3600000
                    timeDifference -= hours * 3600000
                    val amountValue = hours * it.amount + ((timeDifference.toDouble()/3600000.0) * it.amount.toDouble()).toInt()
                    amountView.setText(NumberFormat.getCurrencyInstance().format(amountValue.toDouble()/100))
                }
            }
        }

        val endObserver = Observer<Calendar> { calendar ->
            // Update the UI, in this case, a TextView.
            val timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT) // CHECK add locale
            val dateFormat = DateFormat.getDateInstance(DateFormat.LONG) // CHECK add locale
            //getTimeInstance

            endTimeText.text = timeFormat.format(calendar.time)
            endDateText.text = dateFormat.format(calendar.time)

            if (!newCourseViewModel.manualAmount) {
                newCourseViewModel.rate.value?.let {
                    val startCalendar = newCourseViewModel.startCalendar.value?: Calendar.getInstance()
                    val endCalendar = newCourseViewModel.endCalendar.value?: Calendar.getInstance()
                    //newCourseViewModel.amount.postValue(((endCalendar.timeInMillis - startCalendar.timeInMillis).toDouble() / 3600000 * it.amount).toInt())
                    var timeDifference = endCalendar.timeInMillis - startCalendar.timeInMillis
                    val hours = timeDifference/3600000
                    timeDifference -= hours * 3600000
                    val amountValue = hours * it.amount + ((timeDifference.toDouble()/3600000.0) * it.amount.toDouble()).toInt()
                    amountView.setText(NumberFormat.getCurrencyInstance().format(amountValue.toDouble()/100))
                }
            }
        }

        val skaterObserver = Observer<Skater?> { skater ->
            // Update the UI, in this case, a TextView.
            skater?.let{
                skaterNameText.text = "%s %s".format(it.first_name, it.last_name)
            }
        }

        val rateObserver = Observer<Rate?> { rate ->
            // Update the UI, in this case, a TextView.
            rate?.let{
                rateNameText.text = it.name
                newCourseViewModel.manualAmount = false
                val startCalendar = newCourseViewModel.startCalendar.value?:Calendar.getInstance()
                val endCalendar = newCourseViewModel.endCalendar.value?:Calendar.getInstance()
                //newCourseViewModel.amount.postValue(((endCalendar.timeInMillis - startCalendar.timeInMillis).toDouble()/3600000 * rate.amount).toInt())
                var timeDifference = endCalendar.timeInMillis - startCalendar.timeInMillis
                val hours = timeDifference/3600000
                timeDifference -= hours * 3600000
                val amountValue = hours * it.amount + ((timeDifference.toDouble()/3600000.0) * it.amount.toDouble()).toInt()
                amountView.setText(NumberFormat.getCurrencyInstance().format(amountValue.toDouble()/100))
            }
        }

        newCourseViewModel.startCalendar.observe(this, startObserver)
        newCourseViewModel.endCalendar.observe(this, endObserver)
        newCourseViewModel.skater.observe(this, skaterObserver)
        newCourseViewModel.rate.observe(this, rateObserver)

        amountView.setOnClickListener {
            newCourseViewModel.manualAmount = true
        }
        amountView.addTextChangedListener(object : TextWatcher {
            var current = ""
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString() != current){
                    amountView.removeTextChangedListener(this)

                    val replaceable =
                        String.format("[%s,.]", NumberFormat.getCurrencyInstance().currency.symbol)
                    val cleanString = s.toString().replace(replaceable.toRegex(), "").replace("\\s".toRegex(), "")
                    val parsed = cleanString.toDouble()
                    val formatted = NumberFormat.getCurrencyInstance().format((parsed/100))

                    current = formatted
                    amountView.setText(formatted)
                    amountView.setSelection(formatted.length)

                    amountView.addTextChangedListener(this)
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
                when {
                    newCourseViewModel.startCalendar.value?.compareTo(newCourseViewModel.endCalendar.value)?:0 > 0 -> {
                        val view = findViewById<View>(android.R.id.content)

                        Snackbar.make(view, R.string.end_before_start, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show()
                    }
                    TextUtils.isEmpty(amountView.text) -> {
                        val view = findViewById<View>(android.R.id.content)

                        Snackbar.make(view, R.string.empty_amount, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show()
                    }
                    else -> {
                        val replyIntent = Intent()
                        val name = if (TextUtils.isEmpty(editCourseView.text)) {
                            null
                        } else {
                            editCourseView.text.toString()
                        }
                        val note = if (TextUtils.isEmpty(editNoteView.text)) {
                            null
                        } else {
                            editNoteView.text.toString()
                        }
                        val replaceable =
                            String.format("[%s,.]", NumberFormat.getCurrencyInstance().currency.symbol)
                        val cleanString = amountView.text.toString().replace(replaceable.toRegex(), "").replace("\\s".toRegex(), "")

                        replyIntent.putExtra(EXTRA_NAME, name)
                        replyIntent.putExtra(EXTRA_SKATER, newCourseViewModel.skater.value?.uuid)
                        replyIntent.putExtra(EXTRA_START, newCourseViewModel.startCalendar.value)
                        replyIntent.putExtra(EXTRA_END, newCourseViewModel.endCalendar.value)
                        replyIntent.putExtra(EXTRA_RATE, newCourseViewModel.rate.value?.amount)
                        replyIntent.putExtra(EXTRA_AMOUNT, cleanString.toInt())
                        replyIntent.putExtra(EXTRA_NOTE, note)
                        replyIntent.putExtra(EXTRA_PAID, paidCheckbox.isChecked)

                        setResult(Activity.RESULT_OK, replyIntent)
                        finish()
                    }
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
        TimePickerFragment(newCourseViewModel.startCalendar).show(supportFragmentManager, "StartTimePicker")
    }

    fun showStartDatePickerDialog(@Suppress("UNUSED_PARAMETER") v: View) {
        DatePickerFragment(newCourseViewModel.startCalendar).show(supportFragmentManager, "StartDatePicker")
    }

    fun showEndTimePickerDialog(@Suppress("UNUSED_PARAMETER") v: View) {
        TimePickerFragment(newCourseViewModel.endCalendar).show(supportFragmentManager, "EndTimePicker")
    }

    fun showEndDatePickerDialog(@Suppress("UNUSED_PARAMETER") v: View) {
        DatePickerFragment(newCourseViewModel.endCalendar).show(supportFragmentManager, "EndDatePicker")
    }

    fun showSkaterPickerDialog(@Suppress("UNUSED_PARAMETER") v: View) {
        SkaterPickerFragment(newCourseViewModel.skater, newCourseViewModel.allSkaters).show(supportFragmentManager, "SkaterPicker")
    }

    fun showRatePickerDialog(@Suppress("UNUSED_PARAMETER") v: View) {
        RatePickerFragment(newCourseViewModel.rate, newCourseViewModel.allRates).show(supportFragmentManager, "RatePicker")
    }

    companion object {
        const val EXTRA_NAME = "com.example.android.wordlistsql.REPLY_NAME"
        const val EXTRA_SKATER = "com.example.android.wordlistsql.REPLY_SKATER"
        const val EXTRA_START = "com.example.android.wordlistsql.REPLY_START"
        const val EXTRA_END = "com.example.android.wordlistsql.REPLY_END"
        const val EXTRA_RATE = "com.example.android.wordlistsql.REPLY_RATE"
        const val EXTRA_AMOUNT = "com.example.android.wordlistsql.REPLY_AMOUNT"
        const val EXTRA_NOTE = "com.example.android.wordlistsql.REPLY_NOTE"
        const val EXTRA_PAID = "com.example.android.wordlistsql.REPLY_PAID"
        const val EXTRA_CALENDAR = "com.example.android.wordlistsql.INPUT_CALENDAR"
    }
}
