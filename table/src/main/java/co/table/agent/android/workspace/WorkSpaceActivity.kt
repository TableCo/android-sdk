package co.table.agent.android.workspace

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import co.table.agent.android.BuildConfig
import co.table.agent.android.R
import co.table.agent.android.application.TableApplication
import co.table.agent.android.constans.Common
import co.table.agent.android.constans.Constants
import co.table.agent.android.dashboard.DashboardActivity
import co.table.agent.android.databinding.ActivityWorkspaceBinding
import co.table.agent.android.jetpack.lifecycle.ApiLifeCycle
import co.table.agent.android.login.LoginActivity
import kotlinx.android.synthetic.main.activity_workspace.*

class WorkSpaceActivity : AppCompatActivity() {
    lateinit var workspaceBinding: ActivityWorkspaceBinding
    lateinit var workspaceDataViewModel: WorkspaceDataViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (TableApplication.getAppSession().isAuthenticated()) {
            var intent = Intent(this, DashboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
        workspaceBinding = DataBindingUtil.setContentView(
            this, R.layout.activity_workspace
        )
        workspaceDataViewModel = ViewModelProviders.of(this).get(WorkspaceDataViewModel::class.java)
        ApiLifeCycle(this, this, workspaceDataViewModel)
        workspaceBinding.workspaceViewModel = workspaceDataViewModel
        workspaceBinding.lifecycleOwner = this
        edtWorkspace.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {


            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                var workSpace = s.toString().trim()
                if (workSpace.length > 0) {
                    workspaceDataViewModel.isNextEnable.value = true
                } else {
                    workspaceDataViewModel.isNextEnable.value = false
                }
            }
        })
        if (BuildConfig.DEBUG) {
            edtWorkspace.setText("develop3.dev.table.co")
        }
    }

    fun onNextClick(view: View) {
        var workSpace = edtWorkspace.text.toString().trim()
        if (workSpace.isNotEmpty()) {
            Common.hideKeyboard(this)
            var intent = Intent(this, LoginActivity::class.java)
            intent.putExtra(Constants.B_WORKSPACE, workSpace)
            startActivity(intent)

        }
    }
}
