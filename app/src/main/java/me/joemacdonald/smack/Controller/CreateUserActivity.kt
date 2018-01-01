package me.joemacdonald.smack.Controller

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_create_user.*
import me.joemacdonald.smack.R
import me.joemacdonald.smack.Services.AuthService
import me.joemacdonald.smack.Utilities.BROADCAST_USER_DATA_CHANGE
import java.util.*

class CreateUserActivity : AppCompatActivity() {

    var userAvatar : String = "profileDefault"
    var avatarColour = "[0.5, 0.5, 0.5, 1]"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)

        createSpinner.visibility = ProgressBar.INVISIBLE
    }

    fun enableSpinner(enable: Boolean) {

        if (enable) {
            createSpinner.visibility = ProgressBar.INVISIBLE
        } else {
            createSpinner.visibility = ProgressBar.VISIBLE
        }
        createUserBtn.isEnabled = !enable
        createAvatarImg.isEnabled = !enable
        createAvatarTxt.isEnabled = !enable
    }

    fun errorToast() {
        Toast.makeText(this, "Something went wrong try again", Toast.LENGTH_LONG).show()
        enableSpinner(false)
    }

    fun generateUserAvatar(view: View) {

        val random = Random()
        val colour = random.nextInt(2)
        val avatar = random.nextInt(28)

        userAvatar = when (colour == 0) {
            true -> "light$avatar"
            false -> "dark$avatar"
        }

        val resourceId = resources.getIdentifier(userAvatar, "drawable", packageName)
        createAvatarImg.setImageResource(resourceId)

    }

    fun backgroundColorBtnClicked(view : View) {

        val random = Random()
        val r = random.nextInt(255)
        val g = random.nextInt(255)
        val b = random.nextInt(255)

        createAvatarImg.setBackgroundColor(Color.rgb(r, g, b))

        val savedR = r.toDouble() / 255
        val savedG = r.toDouble() / 255
        val savedB = r.toDouble() / 255

        avatarColour = "[$savedR, $savedG, $savedB]"


    }

    fun createUserBtnClicked(view : View) {

        enableSpinner(true)

        val userName = createUserNameTxt.text.toString()
        val userEmail = createEmailTxt.text.toString()
        val userPassword = createPasswordTxt.text.toString()

        if (userName.isNotEmpty() && userEmail.isNotEmpty() && userPassword.isNotEmpty()) {
            AuthService.registerUser(this, userEmail, userPassword) {
                registerSuccess ->
                if (registerSuccess) {

                    AuthService.loginUser(this, userEmail, userPassword) {
                        loginSuccess ->
                        if (loginSuccess) {
                            AuthService.createUser(this, userName, userEmail, userAvatar, avatarColour) {
                                createSuccess ->
                                if (createSuccess) {

                                    val userDataChange = Intent(BROADCAST_USER_DATA_CHANGE)
                                    LocalBroadcastManager.getInstance(this).sendBroadcast(userDataChange)

                                    enableSpinner(false)
                                    Log.d(TAG, "User successfully created")
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
                    errorToast()
                }
            }
        } else {
            Toast.makeText(this, "Empty fields are not allowed", Toast.LENGTH_LONG).show()
            enableSpinner(false)
        }

    }

}
