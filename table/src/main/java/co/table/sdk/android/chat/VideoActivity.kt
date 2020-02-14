package co.table.sdk.android.chat

import android.Manifest
import android.content.pm.PackageManager
import android.opengl.GLSurfaceView
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import co.table.sdk.android.R
import co.table.sdk.android.application.TableApplication
import co.table.sdk.android.constants.Common
import co.table.sdk.android.constants.Constants
import co.table.sdk.android.databinding.ActivityVideoBinding
import co.table.sdk.android.jetpack.lifecycle.ApiLifeCycle
import co.table.sdk.android.network.API
import co.table.sdk.android.network.ApiResponseInterface
import co.table.sdk.android.views.CustomDialog
import com.opentok.android.*
import kotlinx.android.synthetic.main.activity_video.*

internal class VideoActivity : AppCompatActivity(), ApiResponseInterface, Session.SessionListener,
    PublisherKit.PublisherListener, SubscriberKit.SubscriberListener, Publisher.CameraListener,
    Session.ReconnectionListener {
    private val PERMISSION_REQUEST_CODE: Int = 101
    lateinit var binding: ActivityVideoBinding
    lateinit var viewModel: VideoViewModel
    var sessionId = ""
    var token = ""
    private var mSession: Session? = null
    private var mPublisher: Publisher? = null
    private var mSubscriber: Subscriber? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionId = intent.getStringExtra(Constants.B_SESSION_ID)!!
        token = intent.getStringExtra(Constants.B_TOKEN)!!
        binding = DataBindingUtil.setContentView(
            this, R.layout.activity_video
        )
        viewModel =
            ViewModelProviders.of(this).get(VideoViewModel::class.java)
        ApiLifeCycle(this, this, viewModel)
        binding.videoViewModel = viewModel
        binding.lifecycleOwner = this
        viewModel.userModel.value = TableApplication.getAppSession().currentUser()
        viewModel.getApiKey(API.GET_API_KEY, this)
        checkPermission()

    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (isPermissionGranted()) {
                initializeSession()
            } else {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.RECORD_AUDIO
                    ), PERMISSION_REQUEST_CODE
                )
            }
        } else {
            initializeSession()
        }
    }

    fun isPermissionGranted(): Boolean {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_PHONE_STATE
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        } else {
            return false
        }
    }

    private fun initializeSession() {
        if (viewModel.apiKey.value == null || mSession != null) {
            return
        }

        mSession = Session.Builder(this, "" + viewModel.apiKey.value, sessionId).build()
        mSession!!.setSessionListener(this)
        mSession!!.setReconnectionListener(this)
        mSession!!.connect(token)
        ivDisconnect.setOnClickListener {
            if (mSession != null) {
                mSession!!.disconnect()
            }
        }
        ivCamera.setOnClickListener {
            if (mPublisher != null) {
                mPublisher!!.cycleCamera()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            var granted = true
            for (i in grantResults.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    granted = false
                    val flag: Boolean = ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        permissions[i]
                    )
                    if (flag) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(permissions, PERMISSION_REQUEST_CODE)
                        }
                        break
                    } else {
                        showPermissionInfo()
                        break
                    }
                } else {
                }
            }
            if (granted) {
                initializeSession()
            }

        }
    }

    private fun showPermissionInfo() {
        var dialog = CustomDialog(this)
        dialog.setTitle(getString(R.string.permission))
        dialog.setPositiveButtonVisibility(View.GONE)
        dialog.setNegativeButtonVisibility(View.VISIBLE)
        dialog.setNegativeButtonText(getString(R.string.ok))
        dialog.setMessege(getString(R.string.msg_permission))
        dialog.setOnNegativeClickListener(View.OnClickListener { dialog.dismiss() })
        dialog.setOnDismissListener {
            Common.startInstalledAppDetailsActivity(this)
        }
        dialog.show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    fun onBackClick(view: View) {
        onBackPressed()
    }

    override fun onSuccess(successResponse: Any?, apiTag: String) {
        if (isPermissionGranted())
            initializeSession()
    }

    override fun onFailureDueToServer(errorMessage: Any?, apiTag: String) {

    }

    override fun onFailureRetrofit(message: String?, apiTag: String) {

    }

    override fun logoutUser() {

    }

    override fun noDataFound(apiTag: String) {

    }

    override fun onStreamDropped(p0: Session?, p1: Stream?) {
        if (mSubscriber != null) {
            mSubscriber = null
            subscriberContainer.removeAllViews()
        }
    }

    override fun onStreamReceived(p0: Session?, stream: Stream?) {
        if (mSubscriber == null) {
            mSubscriber = Subscriber.Builder(this, stream).build()
            mSubscriber!!.getRenderer()
                .setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FILL)
            mSubscriber!!.setSubscriberListener(this)
            mSession!!.subscribe(mSubscriber)
            subscriberContainer.addView(mSubscriber!!.getView())
        }
    }

    override fun onConnected(p0: Session?) {
        mPublisher = Publisher.Builder(this).build()
        mPublisher!!.setPublisherListener(this)
        mPublisher!!.setCameraListener(this)
        mPublisher!!.getRenderer().setStyle(
            BaseVideoRenderer.STYLE_VIDEO_SCALE,
            BaseVideoRenderer.STYLE_VIDEO_FILL
        )
        publisherContainer.addView(mPublisher!!.getView())
        if (mPublisher!!.getView() is GLSurfaceView) {
            (mPublisher!!.getView() as GLSurfaceView).setZOrderOnTop(true)
        }
        Log.i("Session Connected", p0?.sessionId)
        mSession!!.publish(mPublisher)
    }

    override fun onCameraChanged(p0: Publisher?, p1: Int) {

    }

    override fun onCameraError(p0: Publisher?, p1: OpentokError?) {
//        Crashlytics.log(Log.ERROR,"Camera",p1?.message)
    }

    override fun onDisconnected(p0: Session?) {
        finish()
    }

    override fun onError(p0: Session?, p1: OpentokError?) {
        Log.i("Session Error", p1?.message)
//        Crashlytics.log(Log.ERROR,"Session",p1?.message)
    }

    override fun onStreamCreated(p0: PublisherKit?, p1: Stream?) {

    }

    override fun onStreamDestroyed(p0: PublisherKit?, p1: Stream?) {

    }

    override fun onError(p0: PublisherKit?, p1: OpentokError?) {
//        Crashlytics.log(Log.ERROR,"Publisher",p1?.message)
    }

    override fun onConnected(p0: SubscriberKit?) {

    }

    override fun onDisconnected(p0: SubscriberKit?) {
        finish()

    }

    override fun onError(p0: SubscriberKit?, p1: OpentokError?) {
//        Crashlytics.log(Log.ERROR,"Subscriber",p1?.message)
    }

    override fun onReconnected(p0: Session?) {

    }

    override fun onReconnecting(p0: Session?) {

    }

    override fun onPause() {
        super.onPause()
        if (mSession != null) {
            mSession!!.onPause()
        }
    }

    override fun onResume() {
        super.onResume()
        if (mSession != null) {
            mSession!!.onResume()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mSession != null) {
            mSession!!.disconnect()
        }
    }
}
