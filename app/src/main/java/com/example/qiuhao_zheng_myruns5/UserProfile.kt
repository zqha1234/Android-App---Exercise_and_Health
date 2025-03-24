package com.example.qiuhao_zheng_myruns5

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.example.qiuhao_zheng_myruns5.R
import java.io.File

class UserProfile : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var changeButton: Button
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private lateinit var tempImgUri: Uri
    private lateinit var saveImgUri: Uri
    private lateinit var textPath: String
    private lateinit var scrollView: ScrollView
    private lateinit var myViewModel: MyViewModel
    private lateinit var cameraResult: ActivityResultLauncher<Intent>
    private lateinit var galleryResult: ActivityResultLauncher<Intent>

    private lateinit var openCam: TextView
    private lateinit var selectGallery: TextView

    private val saveImgFileName = "img.jpg"
    private val tempImgFileName = "temp_img.jpg"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.userprofile)

        // set up tool bar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        imageView = findViewById(R.id.image_view)
        changeButton = findViewById(R.id.change_button)
        saveButton = findViewById(R.id.save_button)
        cancelButton = findViewById(R.id.cancel_button)

//        // find view by id
        val editName = findViewById<EditText>(R.id.name_edit)
        val editEmail = findViewById<EditText>(R.id.email_edit)
        val editPhone = findViewById<EditText>(R.id.phone_edit)
        val editClass = findViewById<EditText>(R.id.class_edit)
        val editMajor = findViewById<EditText>(R.id.major_edit)
        val editGender = findViewById<RadioGroup>(R.id.gender_radio)

        // check permissions
//        Util.checkPermissions(this)


        val tempImgFile = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), tempImgFileName)
        val saveImgFile = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), saveImgFileName)
        tempImgUri =
            FileProvider.getUriForFile(this, "com.example.myruns5.fileprovider", tempImgFile)
        saveImgUri =
            FileProvider.getUriForFile(this, "com.example.myruns5.fileprovider", saveImgFile)

        // file name (saved on phone)
        val fileName = "saveText.txt"
        var directory: String? = null
        directory = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.absolutePath
        // path to save the txt file (include the file name)
        textPath = directory + File.separatorChar + fileName

        // read from the txt in the phone storage
        val info: String = Util.readFromPhone(textPath)
//            println(info) // test use

        // split the info (name, phone, email, gender, class, etc.) read from the phone
        val splitInfo = info.split("\n")
        val splitName =
            splitInfo.find { it.startsWith("name:") }?.substringAfter("name:")?.trim() ?: ""
        val splitPhone =
            splitInfo.find { it.startsWith("phone:") }?.substringAfter("phone:")?.trim() ?: ""
        val splitEmail =
            splitInfo.find { it.startsWith("email:") }?.substringAfter("email:")?.trim() ?: ""
        val splitGender =
            splitInfo.find { it.startsWith("gender:") }?.substringAfter("gender:")?.trim() ?: ""
        val splitClass =
            splitInfo.find { it.startsWith("class:") }?.substringAfter("class:")?.trim() ?: ""
        val splitMajor =
            splitInfo.find { it.startsWith("major:") }?.substringAfter("major:")?.trim() ?: ""
        // initiate the info (name, phone, email, gender, class, etc.) on the app when opening the app - load file
        editName.setText(splitName)
        editPhone.setText(splitPhone)
        editEmail.setText((splitEmail))
        when (splitGender) {
            "Male" -> editGender.check(R.id.male_radio)
            "Female" -> editGender.check(R.id.female_radio)
            else -> editGender.clearCheck()
        }
        editClass.setText(splitClass)
        editMajor.setText(splitMajor)
        // initialize profile photo
        initializeProfilePhoto()

        // change profile photo button
        changeButton.setOnClickListener() {
            val builder = AlertDialog.Builder(this)
            val inflater = LayoutInflater.from(this)
            val dialogView = inflater.inflate(R.layout.profile_pic, null)
            openCam = dialogView.findViewById(R.id.openCamera)
            selectGallery = dialogView.findViewById(R.id.selectFromGallery)
            builder.setView(dialogView)
            builder.setTitle("Pick Profile Picture")
            val dialog = builder.create()

            openCam.setOnClickListener() {
//                println("qz: open cam")
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, tempImgUri)
                cameraResult.launch(intent)
                dialog.dismiss()
            }

            selectGallery.setOnClickListener() {
//                println("qz: open gallery")
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                galleryResult.launch(intent)
                dialog.dismiss()
            }

            dialog.show()

        }

        // Initialize cameraResult
        cameraResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val bitmap = Util.getBitmap(this, tempImgUri)
                    myViewModel.userImage.value = bitmap
                    bitmap?.let {
                        imageView.setImageBitmap(it)
                    }
                }
            }
        // select image from gallery
        galleryResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { sourceUri ->
                    contentResolver.openInputStream(sourceUri)?.use { input ->
                        contentResolver.openOutputStream(tempImgUri)?.use { output ->
                            input.copyTo(output)
                        }
                    }
                    val bitmap = Util.getBitmap(this, tempImgUri)
                    myViewModel.userImage.value = bitmap
                    bitmap?.let {
                        imageView.setImageBitmap(it)
                    }
                }
            }
        }


        myViewModel = ViewModelProvider(this).get(MyViewModel::class.java)
        myViewModel.userImage.observe(this, { it ->
            imageView.setImageBitmap(it)
        })
        // if the saveImgFile exists, that means the user used their own profile photo, otherwise a
        // defaulted photo will be used
        if (saveImgFile.exists()) {
            val bitmap = Util.getBitmap(this, saveImgUri)
            imageView.setImageBitmap(bitmap)
        }

        // save button - save profile photo and info (name, phone, email, gender, class, major) and close app
        saveButton.setOnClickListener {
            // disable saveButton, prevent reclick it
            saveButton.isEnabled = false

            val saveName = editName.text.toString().trim()
            val saveEmail = editEmail.text.toString().trim()
            val savePhone = editPhone.text.toString().trim()
            val selectedGender = editGender.checkedRadioButtonId
            val saveGender = when (selectedGender) {
                R.id.male_radio -> "Male"
                R.id.female_radio -> "Female"
                else -> "Unknown" // if no gender is selected
            }
            val saveClass = editClass.text.toString().trim()
            val saveMajor = editMajor.text.toString().trim()

            // put all the information into a string
            val saveText = buildString {
                append("name: $saveName")
                append("\nemail: $saveEmail")
                append("\nphone: $savePhone")
                append("\ngender: $saveGender")
                append("\nclass: $saveClass")
                append("\nmajor: $saveMajor")
            }

//            println("qz: $saveText")

            // call function in Util.kt to save information
            Util.saveOnPhone(textPath, saveText.toString())

            val tempFilePath = tempImgUri.path
            val saveFilePath = saveImgUri.path
            if (!tempFilePath.isNullOrEmpty() && !saveFilePath.isNullOrEmpty()) {
                val tempFile = File(tempFilePath)
                val saveFile = File(saveFilePath)
                // check if temp.jpg exists
                if (tempFile.exists()) {
                    if (saveFile.exists() || saveFile.createNewFile()) {
                        if (saveFile.canWrite()) {
                            // save the temp.jpg as img.jpb
                            tempFile.copyTo(saveFile, overwrite = true)
                            // delete temp image
                            tempFile.delete()
                        }
                    }
                }
            }

            // exit the app
//            finishAffinity()
            finishAndRemoveTask()
        }


        // cancel button - cancel saving the photo and info and close the app
        cancelButton.setOnClickListener {
            val tempFilePath = tempImgUri.path
            // deltete the temp image
            if (tempFilePath != null) {
                val tempFile = File(tempFilePath)
                tempFile.delete()
            }
            finishAndRemoveTask()
        }

        scrollView = findViewById(R.id.scroll_view)

        editClass.setOnFocusChangeListener { _, _ ->
            addLayoutListener(scrollView, editClass)
        }

        editMajor.setOnFocusChangeListener { _, _ ->
            addLayoutListener(scrollView, editMajor)
        }


    }


    // initialize the profile photo
    private fun initializeProfilePhoto() {
        val file = File(filesDir, "img.jpg")
        if (file.exists()) {
            // if img.jpg exit, load it
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            imageView.setImageBitmap(bitmap)
        } else {
            // if img.jpg doesn't exit, use default photo
            imageView.setImageResource(R.drawable.default_photo)
        }
    }


    // scroll to the right position (for editClass and editMajor)
    private fun addLayoutListener(origin: View, target: View) {
        origin.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            origin.getWindowVisibleDisplayFrame(rect)
            val invisibleHeight = origin.rootView.height - rect.bottom
            val screenHeight = origin.rootView.height
            // check if the keyboard is up, it it is up, scroll the view to the right position -
            // the keyboard won't cover anything
            if (invisibleHeight > screenHeight / 4) {
                val location = IntArray(2)
                target.getLocationInWindow(location)
                val scrollY = location[1] + target.height - rect.bottom
                origin.scrollTo(0, scrollY)
            } else { // the keyboard is down
                origin.scrollTo(0, 0)
            }
        }
    }

    // override the function - onRequesetionPermissionsResult
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        // call Util.requestPermissionsResult
//        Util.requestPermissionsResult(this, requestCode, permissions, grantResults)
//    }

    // menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_optionmenu, menu)
        return true
    }

}