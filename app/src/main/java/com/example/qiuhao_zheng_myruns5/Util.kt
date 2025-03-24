package com.example.qiuhao_zheng_myruns5

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.FileReader
import java.io.FileWriter

object Util {
    // check read, write, and camera permissions
    fun checkPermissions(activity: Activity?) {
        val permissionLists = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        val unCheckedPermissions = permissionLists.any {
            ContextCompat.checkSelfPermission(activity!!, it) != PackageManager.PERMISSION_GRANTED
        }
        if (unCheckedPermissions) {
            ActivityCompat.requestPermissions(activity!!, permissionLists, 0)
        }

    }
    // request permissions
    fun requestPermissionsResult(activity: Activity, requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 0) {
            val reaskPermissions = mutableListOf<String>()
            // get a list of ungranted permissions
            for (i in permissions.indices) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    reaskPermissions.add(permissions[i])
                }
            }
            // for ungranted permissions, reask the ungranted permissions (request the permissions)
            if (reaskPermissions.isNotEmpty()) {
                // reask permission
                ActivityCompat.requestPermissions(activity, reaskPermissions.toTypedArray(), 0)
            }
        }
    }

    // return the image (Bitmap)
    fun getBitmap(context: Context, imgUri: Uri): Bitmap {
        var bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(imgUri))
        val matrix = Matrix()
        matrix.setRotate(90f)
        var ret = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        return ret
    }

    // save name, email etc. information on phone (as .txt file)
    fun saveOnPhone(path: String, txt: String) {
//        println("qz: $path")  // test use only
        var writer: BufferedWriter? = null
        try {
            writer = BufferedWriter(FileWriter(path))
            // write to txt file
            writer.write(txt)
        } catch (e: Exception) {
            e.printStackTrace()
        }finally {
            try {
                // close writer
                writer?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // read name, email etc. information from phone (from .txt file)
    fun readFromPhone(path: String): String {
        var reader: BufferedReader? = null
        val ss = StringBuilder()
        try {
            reader = BufferedReader(FileReader(path))
            var l: String?
            // read line by line
            while (reader.readLine().also { l = it } != null) {
                ss.append(l)
                ss.append("\n")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }finally {
            try {
                // close the reader
                reader?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return ss.toString()
    }
}