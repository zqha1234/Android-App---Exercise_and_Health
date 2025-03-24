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
class DateDialog : DialogFragment(), DialogInterface.OnClickListener, TimePickerDialog.OnTimeSetListener,
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
        // get the current date and return the dialog
        val datePickerDialog = DatePickerDialog(
            requireContext(), this,calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        return datePickerDialog
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
    }
    // date
    override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int) {
//        println("qz: ${year.toString()} / ${monthOfYear + 1} / $dayOfMonth")
        (activity as? MenuEntryFragment)?.getDate(year, monthOfYear, dayOfMonth)
    }
}