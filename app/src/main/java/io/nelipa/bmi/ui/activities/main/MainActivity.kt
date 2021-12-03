package io.nelipa.bmi.ui.activities.main

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import io.nelipa.bmi.R
import io.nelipa.bmi.ui.base.BaseActivity
import io.nelipa.bmi.ui.base.BaseFragment
import io.nelipa.bmi.databinding.ActivityMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : BaseActivity(), BaseFragment.ActivityCommunicationCallback {

    private var binding: ActivityMainBinding? = null

    private val mainVM: MainVM by viewModels()

    private var isReady = false
    private lateinit var content: View
    private val preDrawListener = object : ViewTreeObserver.OnPreDrawListener {
        override fun onPreDraw(): Boolean {
            // Check if the initial data is ready.
            return if (isReady) {
                // The content is ready; start drawing.
                content.viewTreeObserver.removeOnPreDrawListener(this)
                true
            } else {
                // The content is not ready; suspend.
                false
            }
        }
    }

    private val navController: NavController by lazy {
        (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        content = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(preDrawListener)

        lifecycleScope.launch {
            delay(2000)
            // load some data
            isReady = true
        }

        subscribeToNavigationEvents()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun subscribeToNavigationEvents() {
        mainVM.apply {
            subscribeForNavigationEvent()
                .onEach { direction ->
                    navController.navigate(direction)
                }
                .launchIn(lifecycleScope)

            subscribeForPopBackEvent()
                .onEach {
                    navController.popBackStack()
                }
                .launchIn(lifecycleScope)

            subscribeForPopBackToEvent()
                .onEach { destinationId ->
                    navController.popBackStack(destinationId, false)
                }
                .launchIn(lifecycleScope)
        }
    }

    override fun navigate(direction: NavDirections) {
        mainVM.navigateTo(navController.currentDestination, direction)
    }

    override fun popFragment(destination: MainDestination) {
        mainVM.popBack(destination)
    }
}