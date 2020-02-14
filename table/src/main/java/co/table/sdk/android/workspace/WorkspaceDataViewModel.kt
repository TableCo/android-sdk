package co.table.sdk.android.workspace

import androidx.lifecycle.MutableLiveData
import co.table.sdk.android.jetpack.viewmodel.ObservableViewModel


class WorkspaceDataViewModel : ObservableViewModel() {
    var isNextEnable = MutableLiveData<Boolean>()
}
