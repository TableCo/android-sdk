package co.table.agent.android.jetpack.lifecycle

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import co.table.agent.android.jetpack.viewmodel.ObservableViewModel

class ApiLifeCycle(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    val viewModel: ObservableViewModel
) : LifecycleObserver {

    init {
        lifecycleOwner.lifecycle.addObserver(this);


    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResumeScreen() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStopScreen() {
    }
}
