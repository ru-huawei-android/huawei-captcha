package com.sample.huawei.userdetect

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.huawei.hms.common.ApiException
import com.huawei.hms.support.api.safetydetect.SafetyDetect
import com.huawei.hms.support.api.safetydetect.SafetyDetectClient
import com.huawei.hms.support.api.safetydetect.SafetyDetectStatusCodes
import com.sample.huawei.userdetect.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var client: SafetyDetectClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initUserDetectClient()
        binding.CAPTCHA.setOnClickListener {
            onClick(it)
        }
    }

    private fun initUserDetectClient() {
        client = SafetyDetect.getClient(this).apply {
            initUserDetect()
                .addOnSuccessListener {
                    // Indicates that communication with the service was successful.
                    Log.i(TAG, "Communication established")
                }
                .addOnFailureListener {
                    // An error occurred during communication with the service.
                    Log.i(TAG, "Failed to communicate CAPTCHA service")
                }
        }
    }

    override fun onClick(v: View?) {
        client.userDetection(APP_ID)
            .addOnSuccessListener { userDetectResponse ->
                // Indicates that user successfully passed CAPTCHA
                binding.results.text = getString(R.string.success)
                val responseToken = userDetectResponse.responseToken
                Log.i(TAG,"response token: $responseToken")
                if(responseToken.isNotEmpty()) {
                    // Send the response token to your app server, and call the cloud API of
                    // HMS Core on your server to obtain the fake user detection result.
                }
            }
            .addOnFailureListener {  // There was an error communicating with the service.
                binding.results.text = getString(R.string.failure)
                val errorMsg = if (it is ApiException) {
                    // An error with the HMS API contains some additional details.
                    // You can use the apiException.getStatusCode() method to get the status code.
                    "${SafetyDetectStatusCodes.getStatusCodeString(it.statusCode)}: ${it.message}"
                } else {
                    // Unknown type of error has occurred.
                    it.message
                }
                Log.i(TAG, "User detection fail. Error info: $errorMsg")
            }
    }

    companion object {
        const val TAG = "UserDetect"
        const val APP_ID = "105041291"
    }
}