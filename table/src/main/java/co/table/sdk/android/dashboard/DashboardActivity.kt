package co.table.sdk.android.dashboard

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.*
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.table.sdk.TableSDK
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
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions

internal class DashboardActivity : AppCompatActivity(), ApiResponseInterface {

    companion object {
        const val EXTRA_COLOR_INT = "color"
        const val EXTRA_CONVERSATION_ID = "conversation_id"
    }

    private val FILECHOOSER_RESULTCODE = 101
    private var tableId = ""
    lateinit var binding: ActivityDashboardBinding
    lateinit var dashboardDataViewModel: DashboardDataViewModel
    var webViewFileCallback: ValueCallback<Array<Uri>>? = null
    private var showNewMessageMenu = false
    private var initialUrl: String? = null

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
                val i = Intent(Intent.ACTION_GET_CONTENT)
                i.addCategory(Intent.CATEGORY_OPENABLE)
                i.type = "*/*"
                this@DashboardActivity.startActivityForResult(
                    Intent.createChooser(i, getString(R.string.file_chooser)),
                    FILECHOOSER_RESULTCODE
                )
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FILECHOOSER_RESULTCODE) {
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
        if (webView.canGoBack()) {
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

    fun goToJitsi(v: View?) {
        Log.i("jitsiclick", "hi")
        val intent = Intent(this, JitsiVideoActivity::class.java).apply {

        }
        startActivity(intent)
    }

    override fun noDataFound(apiTag: String) {

    }
}
