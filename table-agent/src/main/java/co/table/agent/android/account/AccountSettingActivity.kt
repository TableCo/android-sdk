package co.table.agent.android.account

import android.content.Intent
import android.content.pm.PackageInfo
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import co.table.agent.android.R
import co.table.agent.android.application.TableApplication
import co.table.agent.android.databinding.ActivityAccountBinding
import co.table.agent.android.jetpack.lifecycle.ApiLifeCycle
import co.table.agent.android.workspace.WorkSpaceActivity

class AccountSettingActivity : AppCompatActivity() {
    lateinit var binding: ActivityAccountBinding
    lateinit var accountDataViewModel: AccountDataViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this, R.layout.activity_account
        )
        accountDataViewModel = ViewModelProviders.of(this).get(AccountDataViewModel::class.java)
        ApiLifeCycle(this, this, accountDataViewModel)
        binding.accountViewModel = accountDataViewModel
        binding.lifecycleOwner = this
        val pInfo: PackageInfo = getPackageManager().getPackageInfo(packageName, 0)
        val version = pInfo.versionName
        accountDataViewModel.appVersion.value = version
        accountDataViewModel.userModel.value = TableApplication.getAppSession().currentUser()

    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    fun onBackClick(view: View) {
        onBackPressed()
    }

    fun onLogoutClick(view: View) {

        TableApplication.getAppSession().logout()
        var intent = Intent(this, WorkSpaceActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
