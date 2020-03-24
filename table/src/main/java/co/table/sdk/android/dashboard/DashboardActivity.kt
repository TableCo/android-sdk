package co.table.sdk.android.dashboard

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import co.table.sdk.TableSDK
import co.table.sdk.android.R
import co.table.sdk.android.chat.VideoActivity
import co.table.sdk.android.constants.Constants
import co.table.sdk.android.databinding.ActivityDashboardBinding
import co.table.sdk.android.jetpack.lifecycle.ApiLifeCycle
import co.table.sdk.android.network.API
import co.table.sdk.android.network.ApiResponseInterface
import kotlinx.android.synthetic.main.activity_dashboard.*


internal class DashboardActivity : AppCompatActivity(), ApiResponseInterface {
    private val FILECHOOSER_RESULTCODE = 101
    private var tableId = ""
    lateinit var binding: ActivityDashboardBinding
    lateinit var dashboardDataViewModel: DashboardDataViewModel
    var webViewfileCallback: ValueCallback<Array<Uri>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this, R.layout.activity_dashboard
        )
        dashboardDataViewModel = ViewModelProviders.of(this).get(DashboardDataViewModel::class.java)
        ApiLifeCycle(this, this, dashboardDataViewModel)
        binding.dashboardViewModel = dashboardDataViewModel
        binding.lifecycleOwner = this
        setSupportActionBar(toolbar)
        initWebView()
        dashboardDataViewModel.headerTitle.value = getString(R.string.all_conversation)
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

                if (view != null && url != null && url.isNotEmpty()) {
                    view.loadUrl("javascript:window.android.onUrlChange(window.location.href);")
                }

                progressBar.visibility = View.GONE
            }


            override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
                super.doUpdateVisitedHistory(view, url, isReload)
                if (view!!.canGoBack()) {
                    binding.ivBack.visibility = View.VISIBLE

                    // https://develop3.dev.table.co/conversation/c34ed657-341b-4be2-a08a-4e575b363b7e

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
                    binding.ivBack.visibility = View.VISIBLE
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
                webViewfileCallback = filePathCallback
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
        writeData()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == webViewfileCallback) return
            if (resultCode == Activity.RESULT_OK) {
                if (data != null && data.data != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        webViewfileCallback?.onReceiveValue(
                            WebChromeClient.FileChooserParams.parseResult(
                                resultCode,
                                data
                            )
                        )
                    } else {
                        var array = arrayOf<Uri>(data.data!!)
                        webViewfileCallback?.onReceiveValue(array)
                    }
                    webViewfileCallback = null

                } else {
                    webViewfileCallback?.onReceiveValue(null)
                    webViewfileCallback = null
                }
            } else {
                webViewfileCallback?.onReceiveValue(null)
                webViewfileCallback = null
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

    fun onBackClick(view: View) {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    fun writeData() {
        val keyToken = "authToken"
        val currentUser = TableSDK.appSession.currentUser()
        val tokenValue: String = currentUser!!.token!!

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.evaluateJavascript(
                "window.localStorage.setItem('$keyToken','$tokenValue');",
                {
                    webView.loadUrl(TableSDK.appSession.currentUser()?.workspace + "/table?webview=android&token=" + tokenValue)
                }
            )
        } else {
            webView.loadUrl("javascript:localStorage.setItem('$keyToken','$tokenValue');")
            webView.loadUrl(TableSDK.appSession.currentUser()?.workspace + "/table?webview=android&token=" + tokenValue)
        }
    }

    fun onSettingClick(view: View) {
//        if (webView!!.canGoBack()) {
//            var intent = Intent(this, ConversationSettingActivity::class.java)
//            intent.putExtra(Constants.B_TABLE_ID, tableId)
//            startActivity(intent)
//        } else {
//            var intent = Intent(this, AccountSettingActivity::class.java)
//            startActivity(intent)
//        }
    }

    override fun onSuccess(successResponse: Any?, apiTag: String) {

    }

    override fun onFailureDueToServer(errorMessage: Any?, apiTag: String) {
    }

    override fun onFailureRetrofit(message: String?, apiTag: String) {

    }

    override fun logoutUser() {

    }

    override fun noDataFound(apiTag: String) {

    }
}
