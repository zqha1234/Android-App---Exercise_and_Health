package com.example.qiuhao_zheng_myruns5.dialogs

import androidx.appcompat.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.qiuhao_zheng_myruns5.R

// create dialog view - class example code - DialogFragmentKotlin
class UnitPreferDialog : DialogFragment(), DialogInterface.OnClickListener{
    companion object{
        const val DIALOG_KEY = "dialog"
        const val TEST_DIALOG = 1
    }

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        sharedPreferences = requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        lateinit var ret: Dialog
        val bundle = arguments
        val dialogId = bundle?.getInt(DIALOG_KEY)
        // create dialog view and return it
        if (dialogId == TEST_DIALOG) {
            val builder = AlertDialog.Builder(requireActivity())
            val view: View = requireActivity().layoutInflater.inflate(R.layout.unit_prefer,
                null)
            val editUnitPre = view.findViewById<RadioGroup>(R.id.unit_prefer_radio)
            val meterRadioButton = view.findViewById<RadioButton>(R.id.meter_radio)
            val mileRadioButton = view.findViewById<RadioButton>(R.id.mile_radio)
            // get the unit prefer info from the saved sharedPreference
            val setUnitPrefer = sharedPreferences.getString("unitPrefer", "")
            // restore the setting for the radios
            when (setUnitPrefer) {
                "Meter" -> editUnitPre.check(R.id.meter_radio)
                "Mile" -> editUnitPre.check(R.id.mile_radio)
                else -> editUnitPre.clearCheck()
            }
            builder.setView(view)
            builder.setTitle("Unit Preference")
            // listen meter radio button
            meterRadioButton.setOnClickListener() {
                val editor = sharedPreferences.edit()
                editor.putString("unitPrefer", "Meter")
                editor.apply()
                ret.dismiss()
            }
            // listen mile radio button
            mileRadioButton.setOnClickListener() {
                val editor = sharedPreferences.edit()
                editor.putString("unitPrefer", "Mile")
                editor.apply()
                ret.dismiss()
            }
//            builder.setPositiveButton("ok", this)
            builder.setNegativeButton("cancel", this)
            ret = builder.create()
        }
        return ret
    }

    override fun onClick(dialog: DialogInterface, item: Int) {
        if (item == DialogInterface.BUTTON_NEGATIVE) {
            Toast.makeText(activity, "cancel clicked", Toast.LENGTH_LONG).show()
        }
    }
}