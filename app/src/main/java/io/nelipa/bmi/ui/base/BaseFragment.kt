package io.nelipa.bmi.ui.base

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import io.nelipa.bmi.ui.activities.main.MainDestination
import java.lang.ref.SoftReference

abstract class BaseFragment : Fragment() {

    private lateinit var activityCommunicationCallback: SoftReference<ActivityCommunicationCallback>

    interface ActivityCommunicationCallback {
        fun navigate(direction: NavDirections)
        fun popFragment(destination: MainDestination)
    }

    /*START LIFECYCLE*/

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityCommunicationCallback = SoftReference(context as ActivityCommunicationCallback)
    }

    protected fun navigate(direction: NavDirections) {
        activityCommunicationCallback.get()?.navigate(direction)
    }

    protected fun popFragment(destination: MainDestination = MainDestination.TopDestination) {
        activityCommunicationCallback.get()?.popFragment(destination)
    }
}