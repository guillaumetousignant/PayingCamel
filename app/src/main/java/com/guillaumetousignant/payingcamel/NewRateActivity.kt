package com.guillaumetousignant.payingcamel

import android.app.Activity
import android.content.Intent
import android.icu.text.NumberFormat
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
import com.guillaumetousignant.payingcamel.database.skater.Skater
import com.guillaumetousignant.payingcamel.ui.pickers.SkaterPickerFragment
import com.guillaumetousignant.payingcamel.ui.rates.NewRateViewModel

class NewRateActivity : AppCompatActivity(R.layout.activity_new_rate) {

    private lateinit var newRateViewModel: NewRateViewModel // Added
    private lateinit var editNameView : EditText
    private lateinit var editAmountView : EditText
    private lateinit var skaterNameText: TextView
    private lateinit var editNoteView : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        editNameView = findViewById(R.id.edit_rate_name)
        editNoteView = findViewById(R.id.edit_rate_note)
        editAmountView = findViewById(R.id.rate_amount)
        skaterNameText = findViewById(R.id.rate_skater_name)

        newRateViewModel =
            ViewModelProvider(this).get(NewRateViewModel::class.java)

        setSupportActionBar(findViewById(R.id.new_rate_toolbar))
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp) // set drawable icon
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        window.statusBarColor = getColor(R.color.colorPrimaryDark) // Why is this needed??

        val skaterObserver = Observer<Skater?> { skater ->
            // Update the UI, in this case, a TextView.
            skater?.let{
                skaterNameText.text = "%s %s".format(it.first_name, it.last_name)
            }
        }

        newRateViewModel.skater.observe(this, skaterObserver)

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
                if (TextUtils.isEmpty(editAmountView.text)) {
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
                    replyIntent.putExtra(EXTRA_NAME, name)

                    val replaceable =
                        String.format("[%s,.]", NumberFormat.getCurrencyInstance().currency.symbol)
                    val cleanString = editAmountView.text.toString().replace(replaceable.toRegex(), "").replace("\\s".toRegex(), "")
                    replyIntent.putExtra(EXTRA_AMOUNT, cleanString.toInt())
                    replyIntent.putExtra(EXTRA_SKATER, newRateViewModel.skater.value?.uuid)
                    val note = if (TextUtils.isEmpty(editNoteView.text)) {
                        null
                    } else {
                        editNoteView.text.toString()
                    }
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

    fun showSkaterPickerDialog(v: View) {
        SkaterPickerFragment(newRateViewModel.skater, newRateViewModel.allSkaters).show(supportFragmentManager, "RateSkaterPicker")
    }

    companion object {
        const val EXTRA_AMOUNT = "com.example.android.wordlistsql.REPLY_AMOUNT"
        const val EXTRA_NAME = "com.example.android.wordlistsql.REPLY_NAME"
        const val EXTRA_NOTE = "com.example.android.wordlistsql.REPLY_NOTE"
        const val EXTRA_SKATER = "com.example.android.wordlistsql.REPLY_SKATER"
    }
}
