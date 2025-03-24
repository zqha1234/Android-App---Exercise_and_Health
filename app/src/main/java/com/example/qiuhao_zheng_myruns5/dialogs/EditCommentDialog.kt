package com.example.qiuhao_zheng_myruns5.dialogs

import androidx.appcompat.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.qiuhao_zheng_myruns5.R

// create dialog view - class example code - DialogFragmentKotlin
class EditCommentDialog : DialogFragment(), DialogInterface.OnClickListener{
    companion object{
        const val DIALOG_KEY = "dialog"
        const val TEST_DIALOG = 1
    }

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editSettingComment: EditText

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        sharedPreferences = requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        lateinit var ret: Dialog
        val bundle = arguments
        val dialogId = bundle?.getInt(DIALOG_KEY)
        // create dialog view and return it
        if (dialogId == TEST_DIALOG) {
            val builder = AlertDialog.Builder(requireActivity())
            val view: View = requireActivity().layoutInflater.inflate(
                R.layout.setting_comment,
                null)
            editSettingComment = view.findViewById<EditText>(R.id.editSettingComment)
            val settingComment = sharedPreferences.getString("settingComments", "")
            editSettingComment.setText(settingComment)
            builder.setView(view)
            builder.setTitle("Comment")
            builder.setPositiveButton("ok", this)
            builder.setNegativeButton("cancel", this)
            ret = builder.create()
        }
        return ret
    }

    override fun onClick(dialog: DialogInterface, item: Int) {
        if (item == DialogInterface.BUTTON_POSITIVE) {
            // save info via sharedPreferences
            val editor = sharedPreferences.edit()
            editor.putString("settingComments", editSettingComment.text.toString())
            editor.apply()
            Toast.makeText(activity, "ok clicked", Toast.LENGTH_LONG).show()
        } else if (item == DialogInterface.BUTTON_NEGATIVE) {
            Toast.makeText(activity, "cancel clicked", Toast.LENGTH_LONG).show()
        }
    }
}