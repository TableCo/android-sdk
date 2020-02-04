package co.table.agent.android.account

import androidx.lifecycle.MutableLiveData
import co.table.agent.android.jetpack.viewmodel.ObservableViewModel
import co.table.agent.android.login.UserModel

class AccountDataViewModel : ObservableViewModel() {
    var appVersion = MutableLiveData<String>()
    var userModel = MutableLiveData<UserModel>()

    fun getWorkSpaceName():String{
        var workSpace = userModel.value!!.workspace
        if (workSpace!=null){
            var label = workSpace.replace("https://","")
            label = label.replace(".table.co","")
            return label
        }else{
            return ""
        }
    }
}
