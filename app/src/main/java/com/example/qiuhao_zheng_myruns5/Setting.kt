package com.example.qiuhao_zheng_myruns5

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import com.example.qiuhao_zheng_myruns5.dialogs.EditCommentDialog
import com.example.qiuhao_zheng_myruns5.dialogs.UnitPreferDialog


class Setting : Fragment() {

    private lateinit var userProfile: LinearLayout
    private lateinit var privacySetting: LinearLayout
    private lateinit var unitPrefer: LinearLayout
    private lateinit var settingComments: LinearLayout
    private lateinit var webpage: LinearLayout
    private lateinit var privacyBox: CheckBox
    private lateinit var sharedPreferences: SharedPreferences
//    private lateinit var editSettingComment: EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
//        sharedPreferences = requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        return inflater.inflate(R.layout.setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userProfile = view.findViewById(R.id.userProfile)
        privacySetting = view.findViewById(R.id.privacySetting)
        unitPrefer = view.findViewById(R.id.unitPrefer)
        settingComments = view.findViewById(R.id.settingComments)
        webpage = view.findViewById(R.id.webpage)
        privacyBox = view.findViewById(R.id.privacyBox)
//        editSettingComment = view.findViewById(R.id.editSettingComment)

        sharedPreferences = requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        // get the saved info about privacy setting (checked or not checked)
        privacyBox.isChecked = sharedPreferences.getBoolean("privacyChecked", false)
//        editSettingComment.text = sharedPreferences.getString("settingComments", "")

        userProfile.setOnClickListener {
//            Toast.makeText(context, "User Profile clicked", Toast.LENGTH_SHORT).show() // test use only
            // open an new Activity
            val intent =  Intent(context, UserProfile::class.java)
            startActivity(intent)
        }

        // onclick listener for different features or settings
        privacySetting.setOnClickListener {
            privacyBox.isChecked = !privacyBox.isChecked
            val editor = sharedPreferences.edit()
            editor.putBoolean("privacyChecked", privacyBox.isChecked )
            editor.apply()
        }

        unitPrefer.setOnClickListener {
            editUnitPrefer()
        }

        settingComments.setOnClickListener {
            editComment()
        }

        webpage.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.sfu.ca/computing.html"))
            startActivity(intent)
        }
    }

//    edit unit preference
    private fun editUnitPrefer() {
        val myDialog = UnitPreferDialog()
        val bundle = Bundle()
        bundle.putInt(UnitPreferDialog.DIALOG_KEY, UnitPreferDialog.TEST_DIALOG)
        myDialog.arguments = bundle
        myDialog.show(parentFragmentManager, "unit prefer dialog")
    }

    // edit comment
    private fun editComment() {
        val myDialog = EditCommentDialog()
        val bundle = Bundle()
        bundle.putInt(EditCommentDialog.DIALOG_KEY, EditCommentDialog.TEST_DIALOG)
        myDialog.arguments = bundle
        myDialog.show(parentFragmentManager, "edit comment dialog")
    }

    // edit unit preference
//    private fun editUnitPrefer() {
//        val builder = AlertDialog.Builder(requireContext())
//        val inflater = LayoutInflater.from(requireContext())
//        val dialogView = inflater.inflate(R.layout.unit_prefer, null)
//        val editUnitPre = dialogView.findViewById<RadioGroup>(R.id.unit_prefer_radio)
//        val meterRadioButton = dialogView.findViewById<RadioButton>(R.id.meter_radio)
//        val mileRadioButton = dialogView.findViewById<RadioButton>(R.id.mile_radio)
//        val setUnitPrefer = sharedPreferences.getString("unitPrefer", "")
//        when (setUnitPrefer) {
//            "Meter" -> editUnitPre.check(R.id.meter_radio)
//            "Mile" -> editUnitPre.check(R.id.mile_radio)
//            else -> editUnitPre.clearCheck()
//        }
//        builder.setView(dialogView)
//        builder.setTitle("Unit Preference")
//
//        builder.setNegativeButton("CANCEL") { _, _ ->
//            Toast.makeText(requireContext(), "Cancel", Toast.LENGTH_SHORT).show()
//        }
//
//        val dialog = builder.create()
//        // listen meter radio button
//        meterRadioButton.setOnClickListener() {
//            val editor = sharedPreferences.edit()
//            editor.putString("unitPrefer", "Meter")
//            editor.apply()
//            dialog.dismiss()
//        }
//        // listen mile radio button
//        mileRadioButton.setOnClickListener() {
//            val editor = sharedPreferences.edit()
//            editor.putString("unitPrefer", "Mile")
//            editor.apply()
//            dialog.dismiss()
//        }
//        dialog.show()
//    }


    // edit comment - second way to create dialog view, but it has problem when rotating the sceen
//    private fun editComment() {
//        val builder = AlertDialog.Builder(requireContext())
//        val inflater = LayoutInflater.from(requireContext())
//        val dialogView = inflater.inflate(R.layout.setting_comment, null)
//        val editSettingComment = dialogView.findViewById<EditText>(R.id.editSettingComment)
//        val settingComment = sharedPreferences.getString("settingComments", "")
//        editSettingComment.setText(settingComment)
//
//        builder.setView(dialogView)
//        builder.setTitle("Comment")
//        builder.setPositiveButton("OK") { _, _ ->
//            Toast.makeText(requireContext(), "Comment Saved", Toast.LENGTH_SHORT).show()
//            val editor = sharedPreferences.edit()
//            editor.putString("settingComments", editSettingComment.text.toString())
//            editor.apply()
//        }
//        builder.setNegativeButton("CANCEL") { _, _ ->
//            Toast.makeText(requireContext(), "Cancel", Toast.LENGTH_SHORT).show()
//        }
//
//        val dialog = builder.create()
//        dialog.show()
//    }

}