package co.table.sdk.android.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import co.table.sdk.android.R
import co.table.sdk.android.application.TableApplication
import co.table.sdk.android.constants.Common
import co.table.sdk.android.constants.Constants
import co.table.sdk.android.dashboard.DashboardActivity
import co.table.sdk.android.databinding.ActivityLoginBinding
import co.table.sdk.android.jetpack.lifecycle.ApiLifeCycle
import co.table.sdk.android.network.API
import co.table.sdk.android.network.ApiResponseInterface
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_login.*

internal class LoginActivity : AppCompatActivity(), ApiResponseInterface {

    private val RC_SIGN_IN: Int = 101
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var binding: ActivityLoginBinding
    lateinit var loginDataViewModel: LoginDataViewModel
    var workspace = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this, R.layout.activity_login
        )
        workspace = intent.getStringExtra(Constants.B_WORKSPACE)
        loginDataViewModel = ViewModelProviders.of(this).get(LoginDataViewModel::class.java)
        ApiLifeCycle(this, this, loginDataViewModel)
        loginDataViewModel.workspace.value = workspace
        binding.loginViewModel = loginDataViewModel
        binding.lifecycleOwner = this
        initGoogleSignIn()

    }

    fun onLoginClick(view: View) {

        Common.hideKeyboard(this)
        var email = edtEmail.text.toString()
        var password = edtPassword.text.toString()
        loginDataViewModel.serverError.value = ""
        var isValid = true
        if (email.isEmpty()) {
            isValid = false
            loginDataViewModel.emailError.value = getString(R.string.pls_enter_email)
        } else if (!Common.isEmail(email)) {
            isValid = false
            loginDataViewModel.emailError.value = getString(R.string.pls_enter_valid_email)
        } else {
            loginDataViewModel.emailError.value = ""
        }
        if (password.isEmpty()) {
            isValid = false
            loginDataViewModel.passwordError.value = getString(R.string.pls_enter_password)
        } else if (password.length < 8) {
            isValid = false
            loginDataViewModel.passwordError.value = getString(R.string.pls_enter_valid_password)
        } else {
            loginDataViewModel.passwordError.value = ""
        }


        if (isValid) {

            val params = LoginRequest()
            params.email = email
            params.password = password
            Common.showProgressDialog(this)
            FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
                params.fcm_device_token = it.token
                Common.showProgressDialog(this)
                loginDataViewModel.login(params, API.LOGIN, this)
            }.addOnFailureListener {
//                Crashlytics.log(Log.ERROR, "Firebase Token Failure", it.message)
                loginDataViewModel.login(params, API.LOGIN, this)
            }
        }

    }

    private fun goToNext() {
        var intent = Intent(this, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    fun onGoogleSignClick(view: View) {
        signIn()
    }

    private fun initGoogleSignIn() {
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.server_client_id))
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun signIn() {
        loginDataViewModel.serverError.value = ""
        loginDataViewModel.emailError.value = ""
        loginDataViewModel.passwordError.value = ""
        Common.hideKeyboard(this)
        val signInIntent: Intent = mGoogleSignInClient.getSignInIntent()
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === RC_SIGN_IN) {
            val task =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account =
                completedTask.getResult(ApiException::class.java)
            val params = LoginRequest()
            params.id_token = account?.idToken.toString()
            Common.showProgressDialog(this)
            FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
                params.fcm_device_token = it.token
                loginDataViewModel.googleSignIn(params, API.GOOGLE_SIGNIN, this)

            }.addOnFailureListener {
//                Crashlytics.log(Log.ERROR, "Firebase Token Failure", it.message)
                loginDataViewModel.googleSignIn(params, API.GOOGLE_SIGNIN, this)
            }

        } catch (e: ApiException) {
            e.printStackTrace()
        }
    }

    override fun onSuccess(successResponse: Any?, apiTag: String) {
        Common.dismissProgressDialog()
        if (successResponse is UserModel) {
            successResponse.workspace = loginDataViewModel.validWorkSpace()
            TableApplication.getAppSession().saveSession(successResponse)
            goToNext()
        }
    }

    override fun onFailureDueToServer(errorMessage: Any?, apiTag: String) {
        Common.dismissProgressDialog()
        loginDataViewModel.serverError.value = errorMessage.toString()
    }

    override fun onFailureRetrofit(message: String?, apiTag: String) {
        loginDataViewModel.serverError.value = getString(R.string.pls_enter_valid_workspace)
        Common.dismissProgressDialog()
    }

    override fun logoutUser() {
        Common.dismissProgressDialog()
    }

    override fun noDataFound(apiTag: String) {
        Common.dismissProgressDialog()
    }
}
