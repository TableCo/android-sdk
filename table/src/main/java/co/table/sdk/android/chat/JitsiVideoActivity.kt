package co.table.sdk.android.chat

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

import co.table.sdk.android.R
import co.table.sdk.android.constants.Constants
import org.jitsi.meet.sdk.*
import java.net.MalformedURLException
import java.net.URL

class JitsiVideoActivity : AppCompatActivity() {
    var firstLoad: Boolean = true
    private val HANGUP_RESULTCODE = 976

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jitsi_video)

        val serverURL: URL
        val server: String = "https://" + intent.getStringExtra(Constants.B_SERVER)!! + "/"
        val tenant: String = intent.getStringExtra(Constants.B_TENANT)!!
        val roomID: String = intent.getStringExtra(Constants.B_ROOMID)!!
        val jwt: String = intent.getStringExtra(Constants.B_JWT)!!
        val audio_call = intent.getBooleanExtra(Constants.B_AUDIOCALL, false)
        val options : JitsiMeetConferenceOptions
        serverURL = try {
            URL(server)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
            throw RuntimeException("Invalid server URL!")
        }
        val defaultOptions = JitsiMeetConferenceOptions.Builder()
                .setServerURL(serverURL)
                .setWelcomePageEnabled(false)
                .build()
        JitsiMeet.setDefaultConferenceOptions(defaultOptions)
        val videoOptions = JitsiMeetConferenceOptions.Builder()
            .setRoom("$tenant/$roomID")
            .setToken(jwt)
            .build()
        val audioOptions = JitsiMeetConferenceOptions.Builder()
            .setRoom("$tenant/$roomID")
            .setToken(jwt)
            .setAudioOnly(true)
            .build()

        options = if(audio_call){
            audioOptions
        } else {
            videoOptions
        }

        this.firstLoad = false
        JitsiMeetActivity.launch(this, options)
    }

    override fun onResume() {
        super.onResume()
         if (!firstLoad){
            setResult(HANGUP_RESULTCODE);
             finish()
         }
    }
}
