package me.joemacdonald.smack.Controller

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_create_user.*
import me.joemacdonald.smack.R
import me.joemacdonald.smack.Services.AuthService
import java.util.*

class CreateUserActivity : AppCompatActivity() {

    var userAvatar : String = "profileDefault"
    var avatarColour = "[0.5, 0.5, 0.5, 1]"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
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

        val userName = createUserNameTxt.text.toString()
        val userEmail = createEmailTxt.text.toString()
        val userPassword = createPasswordTxt.text.toString()


        AuthService.registerUser(this, userEmail, userPassword) {
            registerSuccess ->
            if (registerSuccess) {

                AuthService.loginUser(this, userEmail, userPassword) {
                    loginSuccess ->
                    if (loginSuccess) {
                        AuthService.createUser(this, userName, userEmail, userAvatar, avatarColour) {
                            createSuccess ->
                            if (createSuccess) {
                                Log.d(TAG, "User successfully created")
                                finish()
                            }
                        }

                    }
                }
            }
        }

    }

}
