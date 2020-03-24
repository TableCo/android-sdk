package co.table.sdk.android.session

import android.app.Application

//internal class TableApplication : Application() {

//    override fun onCreate() {
//        super.onCreate()
//        session = Session.getInstance(this)
//        getFirebaseToken()
//    }



//        fun getFirebaseToken() {
//            if (session != null && session!!.isAuthenticated()) {
//                FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
//                    sendTokenToServer(it.token)
//                }
//            }
//        }
//
//        private fun sendTokenToServer(token: String) {
//            var tokenRequestModel = TokenRequestModel()
//            tokenRequestModel.fcm_device_token = token
//            ApiClient().getRetrofitObject(
//                session!!.currentUser()!!.workspace,
//                session!!.currentUser()!!.profile!!.token
//            ).sendToken(
//                tokenRequestModel
//            ).enqueue(object : Callback,
//                retrofit2.Callback<TokenResponseModel> {
//                override fun onFailure(call: Call<TokenResponseModel>, t: Throwable) {
//                    Log.d("Token Failure", "Token failure error due to server.")
//                }
//
//                override fun onResponse(
//                    call: Call<TokenResponseModel>,
//                    response: Response<TokenResponseModel>
//                ) {
//                    if (response.code() == 200) {
//                        Log.d("Token Success", "Token added to server.")
//                    } else {
//                        Log.d("Token Failure", "Token failure error due to server.")
//                    }
//                }
//            })
//        }
//    }
//}