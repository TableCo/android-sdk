package co.table.agent.android.workspace

import androidx.lifecycle.MutableLiveData
import co.table.agent.android.jetpack.viewmodel.ObservableViewModel


class WorkspaceDataViewModel : ObservableViewModel() {
    var isNextEnable = MutableLiveData<Boolean>()
}
