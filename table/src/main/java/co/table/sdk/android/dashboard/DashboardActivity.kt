package co.table.sdk.android.dashboard

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.*
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.table.sdk.TableSDK
import co.table.sdk.android.BuildConfig
import co.table.sdk.android.R
import co.table.sdk.android.chat.VideoActivity
import co.table.sdk.android.constants.Common
import co.table.sdk.android.constants.Constants
import co.table.sdk.android.databinding.ActivityDashboardBinding
import co.table.sdk.android.jetpack.lifecycle.ApiLifeCycle
import co.table.sdk.android.network.API
import co.table.sdk.android.network.ApiResponseInterface
import co.table.sdk.android.network.models.CreateConversationResponseModel
import kotlinx.android.synthetic.main.activity_dashboard.*
import co.table.sdk.android.chat.JitsiVideoActivity
import co.table.sdk.android.network.models.GetTableResponseModel
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

const val PERMISSION_REQUEST_CAMERA = 0


internal class DashboardActivity : AppCompatActivity(), ApiResponseInterface,  ActivityCompat.OnRequestPermissionsResultCallback {

    companion object {
        const val EXTRA_COLOR_INT = "color"
        const val EXTRA_CONVERSATION_ID = "conversation_id"
    }

    private val FILECHOOSER_RESULTCODE = 101
    private val HANGUP_RESULTCODE = 976
    private val CAMERA_RESULTCODE = 102
    private val VIDEO_RESULTCODE = 103
    private var tableId = ""
    lateinit var binding: ActivityDashboardBinding
    lateinit var dashboardDataViewModel: DashboardDataViewModel
    var webViewFileCallback: ValueCallback<Array<Uri>>? = null
    private var showNewMessageMenu = false
    private var initialUrl: String? = null
    private var initialBack: Boolean = true

    lateinit var currentPhotoPath: String
    lateinit var currentVideoPath: String
    enum class RequestType {CAMERA, VIDEO}
    var lastPermissionRequestType: RequestType? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this, R.layout.activity_dashboard
        )
        dashboardDataViewModel = ViewModelProviders.of(this).get(DashboardDataViewModel::class.java)
        dashboardDataViewModel.themeColorInt = intent.getIntExtra(EXTRA_COLOR_INT, 0)
        dashboardDataViewModel.initialConversationId = intent.getStringExtra(EXTRA_CONVERSATION_ID)

        ApiLifeCycle(this, this, dashboardDataViewModel)
        binding.dashboardViewModel = dashboardDataViewModel
        binding.lifecycleOwner = this
        setSupportActionBar(toolbar)
        initWebView()
        dashboardDataViewModel.headerTitle.value = getString(R.string.all_conversation)

        // Set up the toolbar
        toolbar.title = getString(R.string.all_conversation)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.navigationIcon = getDrawable(R.drawable.ic_menu)
            toolbar.navigationIcon?.setTint(Color.WHITE)
        }

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        dashboardDataViewModel.getTable(this)

        dashboardDataViewModel.shouldShowNewMessage.observe(this, Observer { invalidateOptionsMenu() })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.new_message, menu)

        // Disable or enable the menu item depending on the view model
        menu?.getItem(0)?.let {
            if (dashboardDataViewModel.shouldShowNewMessage.value!!) {
                it.isEnabled = true
                it.icon?.alpha = 255
            } else {
                it.isEnabled = false
                it.icon?.alpha = 153
            }
        }

        return true
    }

    private fun conversationIdFromUrl(urlString: String): String? {
        val url = Uri.parse(urlString) ?: return null
        val path = url.path ?: return null
        val lastSegment = url.pathSegments.last()

        return if (path.contains("/conversation/") && lastSegment.length == 36) {
            lastSegment
        } else {
            null
        }
    }

    private fun initWebView() {
        val websettings = webView.settings
        websettings.javaScriptEnabled = true
        websettings.databaseEnabled = true
        websettings.domStorageEnabled = true
        websettings.setAppCacheEnabled(true)
        val appCachePath: String = getApplicationContext().getCacheDir().getAbsolutePath()
        websettings.setAppCachePath(appCachePath)
        websettings.allowFileAccess = true
        websettings.setAppCacheEnabled(true)

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
            }

            override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
                super.doUpdateVisitedHistory(view, url, isReload)

                // Only show the new message button when we're on the first conversation screen
                dashboardDataViewModel.shouldShowNewMessage.value = url?.equals(initialUrl) != false

                if (view!!.canGoBack()) {
                    if (url != null && url.isNotEmpty()) {
                        conversationIdFromUrl(url)?.let {
                            this@DashboardActivity.tableId = it
                            dashboardDataViewModel.getHeader(
                                    it,
                                    API.GET_HEADER,
                                    this@DashboardActivity
                            )
                        }
                    }
                } else {
                    dashboardDataViewModel.headerTitle.value = getString(R.string.all_conversation)
                }
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                return true
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                webViewFileCallback = filePathCallback
                selectImage()
                return true
            }

            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                Log.i("Webview Console", consoleMessage?.message())
                return super.onConsoleMessage(consoleMessage)
            }

        }

        webView.addJavascriptInterface(object {
            @JavascriptInterface
            fun videocall(sessionId: String, token: String) {
                var intent = Intent(this@DashboardActivity, VideoActivity::class.java)
                Log.i("sessionId", sessionId)
                Log.i("token", token)
                intent.putExtra(Constants.B_SESSION_ID, sessionId)
                intent.putExtra(Constants.B_TOKEN, token)
                startActivity(intent)
            }
            @JavascriptInterface
            fun jitsicall(server: String, tenant: String, roomID: String, jwt: String, audio_call:Boolean) {
                var intent = Intent(this@DashboardActivity, JitsiVideoActivity::class.java)
                intent.putExtra(Constants.B_SERVER, server)
                intent.putExtra(Constants.B_TENANT, tenant)
                intent.putExtra(Constants.B_ROOMID, roomID)
                intent.putExtra(Constants.B_JWT, jwt)
                intent.putExtra(Constants.B_AUDIOCALL, audio_call)
                startActivityForResult(intent,HANGUP_RESULTCODE)
            }
        }, "mobile")

        val currentUser = TableSDK.appSession.currentUser()
        val tokenValue: String = currentUser!!.token!!

        // Load the fist page, passing the token
        initialUrl = if (dashboardDataViewModel.initialConversationId != null) {
            TableSDK.appSession.currentUser()?.workspace + "/conversation/${dashboardDataViewModel.initialConversationId}?webview=android&token=$tokenValue"
        } else {
            TableSDK.appSession.currentUser()?.workspace + "/conversation?webview=android&token=$tokenValue"
        }
        webView.loadUrl(initialUrl)
    }
    private fun requestCameraPermission(requestType: RequestType) {
        lastPermissionRequestType = requestType
        val permission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        )

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.d("Log", "Permission to photograph denied")
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                PERMISSION_REQUEST_CAMERA
            )
        }
    }

    fun Context.toast(message: CharSequence) =
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_CAMERA -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    webViewFileCallback?.onReceiveValue(null)
                    webViewFileCallback = null
                    print("")
                    Log.d("Log", "Permission has been denied by user")
                    toast("To take a photo, edit camera permissions in app settings");
                } else {
                    Log.d("Log", "Permission has been granted by user")
                    when (lastPermissionRequestType) {
                        RequestType.CAMERA -> dispatchTakePictureIntent()
                        RequestType.VIDEO -> dispatchTakeVideoIntent()
                        null -> toast("Camera permissions are required to attach photos or videos")
                    }
                }
            }
        }
    }

    private fun selectImage() {
        val options: Array<CharSequence> =
            arrayOf<CharSequence>("Take Photo", "Take Video", "Choose From Device", "Cancel")

        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Upload File")
        builder.setItems(options, DialogInterface.OnClickListener { dialog, item ->
            if (options[item] == "Take Photo") {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    // Permission is already available, start camera preview
                    dispatchTakePictureIntent()
                } else {
                    // Permission is missing and must be requested.
                    requestCameraPermission(RequestType.CAMERA)
                }
            } else if (options[item] == "Take Video") {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    // Permission is already available, start camera preview
                    dispatchTakeVideoIntent()
                } else {
                    // Permission is missing and must be requested.
                    requestCameraPermission(RequestType.VIDEO)
                }
            } else if (options[item] == "Choose From Device") {
                val i = Intent(Intent.ACTION_GET_CONTENT)
                i.addCategory(Intent.CATEGORY_OPENABLE)
                i.type = "*/*"
                this@DashboardActivity.startActivityForResult(
                    Intent.createChooser(i, getString(R.string.file_chooser)),
                    FILECHOOSER_RESULTCODE
                )
            } else if (options[item] == "Cancel") {
                webViewFileCallback?.onReceiveValue(null)
                webViewFileCallback = null
                dialog.dismiss()
            }
        })
        val dialog: Dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        dialog.show()
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())

        return File(filesDir, "JPEG_${timeStamp}_" + ".jpg").apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    @Throws(IOException::class)
    private fun createVideoFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())

        return File(filesDir, "MP4_${timeStamp}_" + ".mp4").apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentVideoPath = absolutePath
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
//                    val photoURI: Uri = FileProvider.getUriForFile(this, "co.table.agent.android.fileprovider", it)
                    val photoURI: Uri = FileProvider.getUriForFile(applicationContext, BuildConfig.LIBRARY_PACKAGE_NAME + ".fileprovider", it)

                    currentPhotoPath = photoURI.toString()
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, CAMERA_RESULTCODE)
                }
            }
        }
    }

    private fun dispatchTakeVideoIntent() {
        Intent(MediaStore.ACTION_VIDEO_CAPTURE).also { takeVideoIntent ->
            // Ensure that there's a camera activity to handle the intent
            takeVideoIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val videoFile: File? = try {
                    createVideoFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                videoFile?.also {
                    val videoURI: Uri = FileProvider.getUriForFile(applicationContext, BuildConfig.LIBRARY_PACKAGE_NAME + ".fileprovider", it)

                    currentVideoPath = videoURI.toString()
                    takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoURI)
                    // High quality (either this or 'MMS quality')
                    takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1)
                    // Can also set size & duration limits
//                    i.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 5491520L);//5*1048*1048=5MB
//                    i.putExtra(MediaStore.EXTRA_DURATION_LIMIT,45);
                    startActivityForResult(takeVideoIntent, VIDEO_RESULTCODE)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {

            FILECHOOSER_RESULTCODE -> {
            if (null == webViewFileCallback) return
            if (resultCode == Activity.RESULT_OK) {
                if (data != null && data.data != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        webViewFileCallback?.onReceiveValue(
                            WebChromeClient.FileChooserParams.parseResult(
                                resultCode,
                                data
                            )
                        )
                    } else {
                        var array = arrayOf<Uri>(data.data!!)
                        webViewFileCallback?.onReceiveValue(array)
                    }
                    webViewFileCallback = null

                } else {
                    webViewFileCallback?.onReceiveValue(null)
                    webViewFileCallback = null
                }
            } else {
                webViewFileCallback?.onReceiveValue(null)
                webViewFileCallback = null
            }
        }
            VIDEO_RESULTCODE -> {
                if (null == webViewFileCallback) return
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null && data.data != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                            webViewFileCallback?.onReceiveValue(
                                WebChromeClient.FileChooserParams.parseResult(
                                    resultCode,
                                    data
                                )
                            )
                        } else {
                            var array = arrayOf<Uri>(data.data!!)
                            webViewFileCallback?.onReceiveValue(array)
                        }
                        webViewFileCallback = null

                    } else {
                        webViewFileCallback?.onReceiveValue(null)
                        webViewFileCallback = null
                    }
                } else {
                    webViewFileCallback?.onReceiveValue(null)
                    webViewFileCallback = null
                }
            }

            CAMERA_RESULTCODE -> {
                if (null == webViewFileCallback) return
                if (resultCode == Activity.RESULT_OK) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        val imageIntent = Intent()
                        imageIntent.data = Uri.parse(currentPhotoPath)
                        webViewFileCallback?.onReceiveValue(
                            WebChromeClient.FileChooserParams.parseResult(
                                resultCode,
                                imageIntent
                            )
                        )
                    }
                    webViewFileCallback = null


                } else {
                    webViewFileCallback?.onReceiveValue(null)
                    webViewFileCallback = null
                }
            }
            HANGUP_RESULTCODE -> {
            val js = "window.TableCommand('jitsi-hangup', 1)"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                webView.evaluateJavascript(js) {

                }
            }
        }
    }
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.menuNewMessage -> { onNewMessage(); true }
            android.R.id.home -> { onBackClick(); true }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onBackClick() {
        if (initialBack){
            val currentUser = TableSDK.appSession.currentUser()
            val tokenValue: String = currentUser!!.token!!
            webView.loadUrl(TableSDK.appSession.currentUser()?.workspace + "/conversation?webview=android&token=$tokenValue")
            initialBack = false
        }
        else if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    private fun onNewMessage() {
        Common.showProgressDialog(this)
        dashboardDataViewModel.createConversation(this)
    }

    override fun onSuccess(successResponse: Any?, apiTag: String) {
        when (apiTag) {
            API.CREATE_CONVERSATION -> {
                Common.dismissProgressDialog()
                val conversationResponseModel = successResponse as? CreateConversationResponseModel
                conversationResponseModel?.let {
                    webView.loadUrl(TableSDK.appSession.currentUser()?.workspace + "/conversation/" + it.conversationId)
                }
            }
            API.GET_TABLE -> {
                Common.dismissProgressDialog()
                val conversationResponseModel = successResponse as? GetTableResponseModel
                conversationResponseModel?.let {
                    val token = TableSDK.appSession.currentUser()?.token

                    webView.loadUrl(TableSDK.appSession.currentUser()?.workspace + "/conversation/${it.tableId}?webview=android&token=${token}")
                }

            }
        }
    }

    override fun onFailureDueToServer(errorMessage: Any?, apiTag: String) {
        when (apiTag) {
            API.CREATE_CONVERSATION -> {
                Common.dismissProgressDialog()
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Error")
                builder.setMessage("Error creating new conversation ${errorMessage ?: ""}")
                builder.setCancelable(true)
                val dialog = builder.create()
                dialog.show()
            }
            
        }
    }

    override fun onFailureRetrofit(message: String?, apiTag: String) {
        when (apiTag) {
            API.CREATE_CONVERSATION -> {
                Common.dismissProgressDialog()
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Error")
                builder.setMessage("Error creating new conversation")
                builder.setCancelable(true)
                val dialog = builder.create()
                dialog.show()
            }
        }
    }

    override fun logoutUser() {

    }


    override fun noDataFound(apiTag: String) {

    }
}
