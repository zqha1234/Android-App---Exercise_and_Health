package com.example.qiuhao_zheng_myruns5.dialogs

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.qiuhao_zheng_myruns5.MenuEntryFragment
import java.util.Calendar

// create dialog view - class example code - DialogFragmentKotlin
class TimeDialog : DialogFragment(), DialogInterface.OnClickListener, TimePickerDialog.OnTimeSetListener,
    DatePickerDialog.OnDateSetListener{
    companion object{
        const val DIALOG_KEY = "dialog"
        const val TEST_DIALOG = 1
    }
    private val calendar = Calendar.getInstance()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        lateinit var ret: Dialog
        val bundle = arguments
        val dialogId = bundle?.getInt(DIALOG_KEY)
        // get current time and return the dialog
        val timePickerDialog = TimePickerDialog(
            requireContext(), this,
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE), true
        )
        return timePickerDialog
    }
    // listen the click
    override fun onClick(dialog: DialogInterface, item: Int) {
        if (item == DialogInterface.BUTTON_POSITIVE) {
            Toast.makeText(activity, "ok clicked", Toast.LENGTH_LONG).show()
        } else if (item == DialogInterface.BUTTON_NEGATIVE) {
            Toast.makeText(activity, "cancel clicked", Toast.LENGTH_LONG).show()
        }
    }

    // time
    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
//        println
        (activity as? MenuEntryFragment)?.getTime(hourOfDay, minute)
    }
    // date
    override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int) {

    }
}