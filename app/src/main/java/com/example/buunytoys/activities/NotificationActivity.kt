package com.example.buunytoys.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.buunytoys.R
import com.google.firebase.messaging.FirebaseMessaging
import org.json.JSONException
import org.json.JSONObject

class NotificationActivity : AppCompatActivity() {

    var topic: EditText? = null
    var body: EditText? = null
    var btn_send: Button? = null

    private var requestQue: RequestQueue? = null
    private val URL = "https://fcm.googleapis.com/fcm/send"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        topic = findViewById(R.id.n_title)
        body = findViewById(R.id.n_body)
        btn_send = findViewById(R.id.btn_send)
        requestQue = Volley.newRequestQueue(this)
        FirebaseMessaging.getInstance().subscribeToTopic("subscriber1")

        btn_send?.setOnClickListener(View.OnClickListener { sendNotification() })


    }

    private fun sendNotification() {
        val json = JSONObject()
        try {
            json.put("to", "/topics/" + "subscriber1")
            val notificationObj = JSONObject()
            notificationObj.put("title", topic!!.text.toString())
            notificationObj.put("body", body!!.text.toString())

            json.put("notification", notificationObj)

            val request: JsonObjectRequest = object : JsonObjectRequest(
                Method.POST, URL,
                json,
                Response.Listener { Log.d("TEST", "onResponse: ") },
                Response.ErrorListener { error ->
                    Log.d("TEST", "onError: " + error.networkResponse)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val header: MutableMap<String, String> = HashMap()
                    header["content-type"] = "application/json"
                    header["authorization"] =
                        "key=AAAAgSP6cO4:APA91bHIV0nc_h5zVAaWz75Q5uaWoyLyqPmP0unzR7ywbl6Pp30T19-piXWjd1TaWKFdzKEIjZdMe4-a0ekgiD4mQ_eY-m-x-tKfm71q-aM7-uCzfDscSMH1cNG_-TgZOjOy17cb2jNp"
                    return header
                }
            }
            requestQue!!.add(request)
            Toast.makeText(this, "Sent", Toast.LENGTH_SHORT).show()
            topic!!.setText("")
            body!!.setText("")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }


}

