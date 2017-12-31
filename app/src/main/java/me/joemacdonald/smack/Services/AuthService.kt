package me.joemacdonald.smack.Services

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import me.joemacdonald.smack.Utilities.*
import org.json.JSONObject
import com.android.volley.DefaultRetryPolicy



object AuthService {


    fun registerUser(context: Context, email: String, password: String, complete: (Boolean) -> Unit) {

        val jsonBody = JSONObject()
        jsonBody.put("email", email)
        jsonBody.put("password", password)
        val requestBody = jsonBody.toString()

        val registerRequest = object : StringRequest(Method.POST, URL_REGISTER, Response.Listener { response ->
            Log.d(TAG, response)
            complete(true)
        }, Response.ErrorListener { error ->
            Log.d(TAG, "Could not register user: $error")
            complete(false)
        }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }

        registerRequest.setRetryPolicy(DefaultRetryPolicy(30000, 1, 1f))


        Volley.newRequestQueue(context).add(registerRequest)

    }

}