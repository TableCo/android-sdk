package co.table.sdk.android.account

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import co.table.sdk.android.R
import co.table.sdk.android.application.TableApplication
import co.table.sdk.android.constants.Common
import co.table.sdk.android.constants.Constants
import co.table.sdk.android.databinding.ActivityConversationSettingBinding
import co.table.sdk.android.jetpack.lifecycle.ApiLifeCycle
import co.table.sdk.android.network.API
import co.table.sdk.android.network.ApiResponseInterface

internal class ConversationSettingActivity : AppCompatActivity(), ApiResponseInterface {
    lateinit var binding: ActivityConversationSettingBinding
    lateinit var viewModel: ConversationSettingsViewModel
    var tableId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.hasExtra(Constants.B_TABLE_ID)){
            tableId = intent.getStringExtra(Constants.B_TABLE_ID)
        }
        binding = DataBindingUtil.setContentView(
            this, R.layout.activity_conversation_setting
        )
        viewModel =
            ViewModelProviders.of(this).get(ConversationSettingsViewModel::class.java)
        ApiLifeCycle(this, this, viewModel)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        viewModel.userModel.value = TableApplication.getAppSession().currentUser()
        Common.showProgressDialog(this)
        viewModel.getHeaderDetails(tableId,API.GET_HEADER,this)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    fun onBackClick(view: View) {
        onBackPressed()
    }

    override fun onSuccess(successResponse: Any?, apiTag: String) {
        Common.dismissProgressDialog()
    }

    override fun onFailureDueToServer(errorMessage: Any?, apiTag: String) {
        Common.dismissProgressDialog()
    }

    override fun onFailureRetrofit(message: String?, apiTag: String) {
        Common.dismissProgressDialog()
    }

    override fun logoutUser() {
        Common.dismissProgressDialog()
    }

    override fun noDataFound(apiTag: String) {
        Common.dismissProgressDialog()
    }
}
