package me.joemacdonald.smack.Controller

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import io.socket.client.IO
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import me.joemacdonald.smack.Model.Channel
import me.joemacdonald.smack.R
import me.joemacdonald.smack.Services.AuthService
import me.joemacdonald.smack.Services.MessageService
import me.joemacdonald.smack.Services.UserDataService
import me.joemacdonald.smack.Utilities.*


class MainActivity : AppCompatActivity() {

    val socket = IO.socket(SOCKET_URL)
    lateinit var channelAdapter: ArrayAdapter<Channel>
    var selectedChannel : Channel? = null

    private fun setupAdapters() {

        channelAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, MessageService.channels)
        channel_list.adapter = channelAdapter
    }

    private val userDataChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {

            if (App.prefs.isLoggedIn) {
                userNameNavHeader.text = UserDataService.name
                userEmailNavHeader.text = UserDataService.email
                val resourceId = resources.getIdentifier(UserDataService.avatarName, "drawable", packageName)

                userImageNavHeader.setImageResource(resourceId)
                userImageNavHeader.setBackgroundColor(UserDataService.returnAvatarColor(UserDataService.avatarColor))
                loginBtnNavHeader.text = getString(R.string.logout_text)

                MessageService.getChannels() {
                    complete ->
                    if (complete) {
                        if (MessageService.channels.count() > 0 ) {
                            selectedChannel = MessageService.channels[0]
                            channelAdapter.notifyDataSetChanged()
                            updateWithChannel()
                        }
                    }

                }
            }

        } // onReceive()
    } // userDataCangeReceiver

    fun updateWithChannel() {

        mainChannelName.text = "#${selectedChannel?.name}"
        // Download messages for channel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        socket.connect()
        socket.on("channelCreated", onNewChannel)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        setupAdapters()

        channel_list.setOnItemClickListener { _, _, position, _ ->
            selectedChannel = MessageService.channels[position]
            drawer_layout.closeDrawer(GravityCompat.START)
            updateWithChannel()
        }

        if (App.prefs.isLoggedIn) {
            AuthService.findUserByEmail(this) {}
        }

    }

    override fun onResume() {

        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangeReceiver, IntentFilter(BROADCAST_USER_DATA_CHANGE))

        super.onResume()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        socket.disconnect()

        LocalBroadcastManager.getInstance(this).unregisterReceiver(userDataChangeReceiver)

        super.onDestroy()
    }

    fun loginBtnNavClicked( ) {

        if (App.prefs.isLoggedIn) {

            UserDataService.logout()

            userNameNavHeader.text = ""
            userEmailNavHeader.text = ""
            userImageNavHeader.setImageResource(R.drawable.profiledefault)
            userImageNavHeader.setBackgroundColor(Color.TRANSPARENT)
            loginBtnNavHeader.text = getString(R.string.login_text)
        } else {
            // Login
            val loginIntent : Intent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
    }

    fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if (inputManager.isAcceptingText) {
            inputManager.hideSoftInputFromWindow(currentFocus.windowToken, 0 )
        }
    }


    fun addChannelBtnClicked( ) {

        if (App.prefs.isLoggedIn) {
            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.add_channel_dialog, null)

            builder.setView(dialogView)
                    .setPositiveButton("Add") { _, _ ->
                        // perform logic when clicked
                        val nameTxt = dialogView.findViewById<EditText>(R.id.addChannelNameTxt)
                        val descTxt = dialogView.findViewById<EditText>(R.id.addChannelDescTxt)

                        val channelName = nameTxt.text.toString()
                        val channelDesc = descTxt.text.toString()

                        // create channel with channel name and description
                        socket.emit("newChannel", channelName, channelDesc)


                    }
                    .setNegativeButton("Cancel") { _, _ ->
                        // cancel and close the dialog
                    }
                    .show()
        }
    }

    private val onNewChannel = Emitter.Listener {
        args ->
        runOnUiThread {
            val channelName = args[0] as String
            val channelDescription = args[1] as String
            val channelId = args[2] as String

            val newChannel = Channel(channelName, channelDescription, channelId)

            MessageService.channels.add(newChannel)
            Log.d(TAG, " ${newChannel.id} : ${newChannel.name} : ${newChannel.description}")
        }
    }

    fun sendMsgBtnClicked( view: View) {

        hideKeyboard()
    }

}
