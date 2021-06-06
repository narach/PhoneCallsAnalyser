package com.example.phonecallsanalyser.listeners

import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log


class PhoneCallStartEndDetector() : PhoneStateListener() {

    private val logTag = "PhoneCallStartEndDetector"

    private var lastState = TelephonyManager.CALL_STATE_IDLE
    var isIncoming: Boolean = false
    var isIncomingPicked = false
    var isOutgoingStarted = false
    var context: Context? = null
    var savedNumber: String? = null
    var contactName: String? = null

    fun setLocalContext(context: Context) {
        this.context = context
    }

    override fun onCallStateChanged(state: Int, phoneNumber: String?) {
        super.onCallStateChanged(state, phoneNumber)

        if (lastState == state) {
            return
        }

        when(state) {
            TelephonyManager.CALL_STATE_RINGING -> {
                isIncoming = true
                savedNumber = phoneNumber
            }
            TelephonyManager.CALL_STATE_OFFHOOK -> {
                if(lastState != TelephonyManager.CALL_STATE_RINGING) {
                    isIncoming = false
                    isIncomingPicked = false
                    isOutgoingStarted = true
                } else if(lastState == TelephonyManager.CALL_STATE_RINGING) {
                    isIncoming = true
                    isIncomingPicked = true
                    isOutgoingStarted = false
                }
            }
            TelephonyManager.CALL_STATE_IDLE -> {
                if (lastState == TelephonyManager.CALL_STATE_RINGING) {
                    // Process finishing a call - get call duration for example
                } else if(isOutgoingStarted) {
                    contactName = getContactName(savedNumber!!, context)
                    Log.d(logTag, "Contact Name: $contactName")
                } else if (isIncoming || isIncomingPicked) {
                    contactName = getContactName(savedNumber!!, context)
                    Log.d(logTag, "Contact Name: $contactName")
                }
            }
        }
        lastState = state
    }

    private fun getContactName(number: String, context: Context?) : String {
        var contactNumber = ""
        var contactName = ""

        // define the columns I want the query to return from Contacts
        val projection = arrayOf(
            ContactsContract.PhoneLookup.DISPLAY_NAME,
            ContactsContract.PhoneLookup.NUMBER,
            ContactsContract.PhoneLookup.HAS_PHONE_NUMBER
        )

        var contactUri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(number)
        )

        var cursor = context?.contentResolver?.query(
            contactUri,
            projection,
            null, null, null
        )

        if(cursor!!.moveToFirst()) {
            contactName = cursor.getString(
                cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)
            )
        }
        cursor.close()

        return if (contactNumber == "") number else contactName
    }
}