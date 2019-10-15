package com.guillaumetousignant.payingcamel.ui.new_course

import com.guillaumetousignant.payingcamel.R
import android.os.Bundle
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager


class NewCourseDialog : DialogFragment() {

    private var toolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppTheme_FullScreenDialog)
    }

    override fun onStart() {
        super.onStart()
        dialog?.let {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            it.window?.setLayout(width, height)
            it.window?.setWindowAnimations(R.style.AppTheme_Slide)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.new_course_dialog, container, false)

        toolbar = view.findViewById(R.id.new_course_dialog_toolbar)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar?.setNavigationOnClickListener{ v ->
            dismiss()
        }
        toolbar?.title = getString(R.string.new_course_dialog_title)
        toolbar?.inflateMenu(R.menu.new_word_dialog_menu)
        toolbar?.setOnMenuItemClickListener { item ->
            dismiss()
            true
        }
    }

    companion object {
        val TAG = "example_dialog"

        fun display(fragmentManager: FragmentManager): NewCourseDialog {
            val newCourseDialog = NewCourseDialog()
            newCourseDialog.show(fragmentManager, TAG)
            return newCourseDialog
        }
    }
}