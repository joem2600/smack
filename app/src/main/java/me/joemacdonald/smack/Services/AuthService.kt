package me.joemacdonald.smack.Services

import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import me.joemacdonald.smack.Utilities.*
import org.json.JSONObject
import com.android.volley.DefaultRetryPolicy
import com.android.volley.toolbox.JsonObjectRequest
import me.joemacdonald.smack.Controller.App
import org.json.JSONException


object AuthService {

//    var isLoggedIn = false
//    var userEmail = ""
//    var authToken = ""

    fun registerUser(email: String, password: String, complete: (Boolean) -> Unit) {

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

        registerRequest.setRetryPolicy( DefaultRetryPolicy(30000, 1, 1f) )

        App.prefs.requestQueue.add(registerRequest)
    }

    fun loginUser(email: String, password: String, complete: (Boolean) -> Unit) {

        val jsonBody = JSONObject()
        jsonBody.put("email", email)
        jsonBody.put("password", password)
        val requestBody = jsonBody.toString()

        val loginRequest = object : JsonObjectRequest(Method.POST, URL_LOGIN, null, Response.Listener {
            // this is where we parse the json object
            response ->
            try {
                Log.d(TAG, response.toString())
                App.prefs.userEmail = response.getString("user")
                App.prefs.authToken = response.getString("token")
                App.prefs.isLoggedIn = true
                complete(true)
            } catch (e: JSONException) {
                Log.d(TAG, e.localizedMessage)
                complete(false)
            }
        }, Response.ErrorListener {
            // This is where we deal with our errors
            error ->
            Log.d(TAG, "Login failed: $error")
            complete(false)
        }) {
            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }
        }

        App.prefs.requestQueue.add(loginRequest)
    }

    fun createUser( name: String, email: String, avatarName: String, avatarColor: String, complete: (Boolean) -> Unit) {

        val jsonBody = JSONObject()
        jsonBody.put("name", name)
        jsonBody.put("email", email)
        jsonBody.put("avatarName", avatarName)
        jsonBody.put("avatarColor", avatarColor)
        val requestBody = jsonBody.toString()

        val createUserRequest = object : JsonObjectRequest(Method.POST, URL_CREATE_USER, null, Response.Listener {
            // parse JSON object
            response ->
            try {
                UserDataService.name = response.getString("name")
                UserDataService.email = response.getString("email")
                UserDataService.avatarName = response.getString("avatarName")
                UserDataService.avatarColor = response.getString("avatarColor")
                UserDataService.id = response.getString("_id")
                complete(true)
            } catch (e: JSONException) {
                Log.d(TAG, e.localizedMessage)
                complete(false)
            }
        }, Response.ErrorListener {
            // parse errors
            error ->
            Log.d(TAG, "Create user failed: $error")
            complete(false)
        }) {
            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Authorization", "Bearer ${App.prefs.authToken}")
                return headers
            }
        }

        App.prefs.requestQueue.add(createUserRequest)
    }

    fun findUserByEmail(context: Context, complete: (Boolean) -> Unit) {

        val findUserRequest = object: JsonObjectRequest(Method.GET, "$URL_GET_USER${App.prefs.userEmail}", null, Response.Listener {
            // parse request
            response ->
            try {
                UserDataService.name = response.getString("name")
                UserDataService.email = response.getString("email")
                UserDataService.avatarName = response.getString("avatarName")
                UserDataService.avatarColor = response.getString("avatarColor")
                UserDataService.id = response.getString("_id")

                val userDataChange = Intent(BROADCAST_USER_DATA_CHANGE)
                LocalBroadcastManager.getInstance(context).sendBroadcast(userDataChange)

                complete(true)
            } catch (e: JSONException) {
                Log.d(TAG, "EXC: ${e.localizedMessage}")
                complete(false)
            }
            complete(true)
        }, Response.ErrorListener {
            // parse error
            Log.d(TAG, "Could not find user")
            complete(false)
        }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Authorization", "Bearer ${App.prefs.authToken}")
                return headers
            }
        }

        App.prefs.requestQueue.add(findUserRequest)

    }
}














































