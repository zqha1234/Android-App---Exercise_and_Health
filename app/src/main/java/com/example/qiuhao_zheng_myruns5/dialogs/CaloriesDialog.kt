package com.example.qiuhao_zheng_myruns5.dialogs

import androidx.appcompat.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.qiuhao_zheng_myruns5.MenuEntryFragment
import com.example.qiuhao_zheng_myruns5.R


class CaloriesDialog : DialogFragment(), DialogInterface.OnClickListener{
    companion object{
        const val DIALOG_KEY = "dialog"
        const val TEST_DIALOG = 1
    }

    private lateinit var view: View

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        lateinit var ret: Dialog
        val bundle = arguments
        val dialogId = bundle?.getInt(DIALOG_KEY)
        if (dialogId == TEST_DIALOG) {
            val builder = AlertDialog.Builder(requireActivity())
            view = requireActivity().layoutInflater.inflate(
                R.layout.calories,
                null)
            builder.setView(view)
            builder.setTitle("Calories")
            builder.setPositiveButton("ok", this)
            builder.setNegativeButton("cancel", this)
            ret = builder.create()
        }
        return ret
    }

    override fun onClick(dialog: DialogInterface, item: Int) {
        if (item == DialogInterface.BUTTON_POSITIVE) {
            val caloriesText = view.findViewById<EditText>(R.id.editCalories)
            val calories = caloriesText.text.toString()
            if (calories.isNotEmpty()) {
                (activity as? MenuEntryFragment)?.getCalories(calories)
            }
//            Toast.makeText(activity, "ok clicked", Toast.LENGTH_LONG).show()
        } else if (item == DialogInterface.BUTTON_NEGATIVE) {
            Toast.makeText(activity, "cancel clicked", Toast.LENGTH_LONG).show()
        }
    }
}