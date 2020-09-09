package com.example.readwritetxt

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter

class MainActivity : AppCompatActivity() {

    private val PermissionsRequestCode = 123
    private lateinit var managePermissions: ManagePermissions
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val list = listOf<String>(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        managePermissions = ManagePermissions(this, list, PermissionsRequestCode)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            managePermissions.checkPermissions()
        data class dclass1(val headerSmall: String = "", val headerSmallText: String = "")
        val dclasslistArray: MutableList<List<dclass1>> = ArrayList()
        for (i in 0..9) dclasslistArray.add(ArrayList())
        var dclasscount = 0
        var dcclassnamescount = 0
        var headerSmallH = ""
        var headerSmallTextH = listOf<String>()
        var dclassnames = listOf<String>()
        val sdcard = Environment.getExternalStorageDirectory()
        readfile.setOnClickListener {
            try {
                var txtfile = File(sdcard.absolutePath,"filename.txt")
                if (txtfile.exists()) {
                    var txtfileList = txtfile.readLines()
                    var i = 0
                    while (i < txtfileList.size){

                        //i need to separate big headers with %
                        if (txtfileList.get(i).startsWith("%")) {
                            dclassnames += txtfileList[i].removePrefix("%")
                            dcclassnamescount++
                            if (dcclassnamescount > 1) dclasscount++
                        }

                        //separate smaller headers (deviceName in data class) with @
                        if (txtfileList[i].startsWith("@")) {
                            headerSmallH = txtfileList[i]
                            headerSmallTextH = emptyList()
                            i++
                            //fill headerSmall with headerSmallText
                            while (!txtfileList[i].startsWith("$")) {
                                headerSmallTextH += txtfileList[i]
                                i++
                            }
                            dclasslistArray[dclasscount] = dclasslistArray[dclasscount] + dclass1(headerSmallH, headerSmallTextH.toString())
                        }
                        i++
                    }
                    toast("reading successful")
                } else toast("txt was not found")

            }  catch (ioe: IOException) {
                toast("unable to read txt")
            }
        }
        check.setOnClickListener {
            statustext.text = ((dclasslistArray[2])[1].headerSmallText).replace("[","").replace("]","") + "\n${getString(R.string.emoji)}"
        }
        createfile.setOnClickListener {
            try {
                var file = File(sdcard.absolutePath, "testfile.txt")
                val osw = OutputStreamWriter(FileOutputStream(file))
                osw.write(((dclasslistArray[2])[1].headerSmallText))
                osw.flush()
                osw.close()
                toast("testfile.txt created")
            }
            catch (ioe: IOException) {
                toast("unable to create txt")

            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            PermissionsRequestCode -> {
                val isPermissionsGranted = managePermissions
                    .processPermissionsResult(requestCode, permissions, grantResults)
                if (isPermissionsGranted) {
                    toast("permissions was granted")
                } else toast("permissions was not granted")
                return
            }

        }
    }
}
fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}