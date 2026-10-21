
package com.codepath.lab6

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.codepath.lab6.databinding.ActivityMainBinding
import kotlinx.serialization.json.Json

// Keep this helper because ParksFragment uses it
fun createJson() = Json {
    isLenient = true
    ignoreUnknownKeys = true
    useAlternativeNames = false
}

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var activeFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        // Load the user's saved dark mode preference
        val preferences = getSharedPreferences(
            "CampgroundSettings",
            Context.MODE_PRIVATE
        )

        val darkModeEnabled = preferences.getBoolean(
            "dark_mode",
            false
        )

        // Apply dark mode before creating the activity
        AppCompatDelegate.setDefaultNightMode(
            if (darkModeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Restore the visible fragment after rotation
        activeFragment = supportFragmentManager.fragments
            .firstOrNull {
                it.isAdded && !it.isHidden
            }

        // Set up bottom navigation with four tabs
        binding.bottomNavigation.setOnItemSelectedListener { item ->

            val tag = when (item.itemId) {

                R.id.navigation_home -> "home"

                R.id.navigation_parks -> "parks"

                R.id.navigation_campgrounds -> "campgrounds"

                R.id.navigation_settings -> "settings"

                else -> return@setOnItemSelectedListener false
            }

            showFragment(tag)
            true
        }

        // Set Home as the default tab on first launch
        if (savedInstanceState == null) {
            binding.bottomNavigation.selectedItemId =
                R.id.navigation_home
        } else {

            // Restore the selected tab after rotation
            val restoredTag = activeFragment?.tag

            val selectedId = when (restoredTag) {

                "home" -> R.id.navigation_home

                "parks" -> R.id.navigation_parks

                "campgrounds" -> R.id.navigation_campgrounds

                "settings" -> R.id.navigation_settings

                else -> R.id.navigation_home
            }

            binding.bottomNavigation.selectedItemId = selectedId
        }
    }

    private fun showFragment(tag: String) {

        val fragmentManager = supportFragmentManager

        val existingFragment =
            fragmentManager.findFragmentByTag(tag)

        // Avoid recreating the currently displayed fragment
        if (activeFragment?.tag == tag) {
            return
        }

        // Reuse an existing fragment or create a new one
        val fragment = existingFragment ?: when (tag) {

            "home" -> HomeFragment()

            "parks" -> ParksFragment()

            "campgrounds" -> CampgroundFragment()

            "settings" -> SettingsFragment()

            else -> return
        }

        val transaction = fragmentManager.beginTransaction()
            .setReorderingAllowed(true)

        // Hide the currently visible fragment
        activeFragment?.let {
            transaction.hide(it)
        }

        // Add a new fragment or show an existing one
        if (existingFragment == null) {
            transaction.add(
                R.id.main_frame_layout,
                fragment,
                tag
            )
        } else {
            transaction.show(fragment)
        }

        transaction.commit()

        activeFragment = fragment
    }
}
