package co.table.sdk.android.chat

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

import co.table.sdk.android.R
import org.jitsi.meet.sdk.JitsiMeet
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import java.net.MalformedURLException
import java.net.URL

class JitsiVideoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jitsi_video)

        // Initialize default options for Jitsi Meet conferences.
        val serverURL: URL
        val tenant: String = ""
        val roomID: String = ""
        val jwt: String = ""

        serverURL = try {
            // When using JaaS, replace "https://meet.jit.si" with the proper serverURL
            URL("https://meet.jit.si")
        } catch (e: MalformedURLException) {
            e.printStackTrace()
            throw RuntimeException("Invalid server URL!")
        }
        val defaultOptions = JitsiMeetConferenceOptions.Builder()
                .setServerURL(serverURL)
            .setRoom("$tenant/$roomID")
                .setToken(jwt)
                .setWelcomePageEnabled(false)

                .build()
        JitsiMeet.setDefaultConferenceOptions(defaultOptions)
    }

    fun onButtonClick(v: View?) {
        val editText = findViewById<EditText>(R.id.conferenceName)
        val text = editText.text.toString()

            // Build options object for joining the conference. The SDK will merge the default
            // one we set earlier and this one when joining.
            val options = JitsiMeetConferenceOptions.Builder()
                    .setRoom("TiredBasketballsWeakenRelatively")
                    .build()
            // Launch the new activity with the given options. The launch() method takes care
            // of creating the required Intent and passing the options.
            JitsiMeetActivity.launch(this, options)

    }
}
