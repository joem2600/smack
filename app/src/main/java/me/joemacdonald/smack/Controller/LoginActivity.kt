package me.joemacdonald.smack.Controller

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*
import me.joemacdonald.smack.R
import me.joemacdonald.smack.Services.AuthService

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginSpinner.visibility = ProgressBar.INVISIBLE
    }

    fun hideKeyboard() {

        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if (inputManager.isAcceptingText) {
            inputManager.hideSoftInputFromWindow(currentFocus.windowToken, 0 )
        }


    }

    fun errorToast() {
        Toast.makeText(this, "Something went wrong try again", Toast.LENGTH_LONG).show()
        enableSpinner(false)
    }

    fun enableSpinner(enable: Boolean) {

        if (enable) {
            loginSpinner.visibility = ProgressBar.INVISIBLE
        } else {
            loginSpinner.visibility = ProgressBar.VISIBLE
        }
//        createUserBtn.isEnabled = !enable
//        createAvatarImg.isEnabled = !enable
//        createAvatarTxt.isEnabled = !enable
    }
    fun loginLoginBtnClicked(view: View) {

        enableSpinner(true)

        val email = loginEmailTxt.text.toString()
        val password = loginPasswordTxt.text.toString()

        hideKeyboard()

        if (email.isNotEmpty() && password.isNotEmpty() ) {
            AuthService.loginUser(this, email, password) { loginSuccess ->
                if (loginSuccess) {
                    AuthService.findUserByEmail(this) { findSuccess ->
                        if (findSuccess) {
                            enableSpinner(false)
                            finish()
                        } else {
                            errorToast()
                        }
                    }
                } else {
                    errorToast()
                }
            }
        } else {
            Toast.makeText(this, "Fill in email and password", Toast.LENGTH_LONG)
        }
    }

    fun loginCreateUserBtnClicked(view : View) {

        val createUserIntent : Intent = Intent(this, CreateUserActivity::class.java)
        startActivity(createUserIntent)

    }
}
