package com.example.phonecallsanalyser

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.CallLog
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.phonecallsanalyser.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    val READ_PHONE_STATE_CODE = 369

    private lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            btnStartCallLog.setOnClickListener {
                tvLogs.text = "Call Logging Started..."
                var stringOutput = ""
                var uriCallLogs = Uri.parse("content://call_log/calls")
                var cursorCallLogs = contentResolver.query(uriCallLogs,
                    null, null, null)
                cursorCallLogs!!.moveToFirst()

                do {
                    val stringNumber = cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls.NUMBER))
                    val stringName = cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls.CACHED_NAME))
                    val stringDuration = cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls.DURATION))
                    val stringType = cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls.TYPE))

                    stringOutput += "Duration: $stringDuration, number: $stringNumber, " +
                            "name: $stringName, type: $stringType"
                } while (cursorCallLogs.moveToNext())

                tvLogs.text = stringOutput
            }
        }

        /*
        * Get Permission to process phone calls
        * */
        if(ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_PHONE_STATE
        ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.READ_CALL_LOG),
                READ_PHONE_STATE_CODE
            )
        }
    }
}