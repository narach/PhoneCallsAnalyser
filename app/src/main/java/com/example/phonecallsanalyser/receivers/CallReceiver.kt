package com.example.phonecallsanalyser.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import com.example.phonecallsanalyser.listeners.PhoneCallStartEndDetector

class CallReceiver : BroadcastReceiver() {

    val LOG_TAG = "CallReceiver"

    private var listener: PhoneCallStartEndDetector? = null
    private var context: Context? = null

    override fun onReceive(context: Context?, intent: Intent?) {

        this.context = context
        if (listener == null) {
            listener = PhoneCallStartEndDetector()
            // Set custom PhoneStateListener
            val telephonyManager = context!!.getSystemService(Context.TELEPHONY_SERVICE)
                as TelephonyManager
            telephonyManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE)
        }
        listener?.setLocalContext(context!!)

        val callState = intent!!.getStringExtra(TelephonyManager.EXTRA_STATE)
        val extraData = intent.extras
        Log.d(LOG_TAG, "Available extras: $extraData")
//        TelephonyManager.EXTRA_

        when (callState) {
            TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                showToastMsg(context!!, "Phone Call is Started...")
                Log.d(LOG_TAG, "Phone Call is Started...")
            }
            TelephonyManager.EXTRA_STATE_IDLE -> {
                showToastMsg(context!!, "Phone Call is Ended...")
                Log.d(LOG_TAG, "Phone Call is Ended...")
            }
            TelephonyManager.EXTRA_STATE_RINGING -> {
                showToastMsg(context!!, "Incoming Call...")
                Log.d(LOG_TAG, "Incoming Call...")
            }
        }

        // Get Additional call info
//        val telephonyManager = context!!.getSystemService(Context.TELEPHONY_SERVICE)
//                as TelephonyManager
//        telephonyManager.listen(object : PhoneStateListener() {
//            override fun onCallStateChanged(state: Int, phoneNumber: String?) {
//                super.onCallStateChanged(state, phoneNumber)
//                Log.d(LOG_TAG, "Incoming number: $phoneNumber")
//            }
//        }, PhoneStateListener.LISTEN_CALL_STATE)
    }

    private fun showToastMsg(c: Context, msg: String) {
        val toast = Toast.makeText(c, msg, Toast.LENGTH_LONG)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }
}