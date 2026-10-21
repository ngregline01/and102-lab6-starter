package com.codepath.lab6

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.fragment_settings,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        val darkModeSwitch =
            view.findViewById<SwitchCompat>(R.id.darkModeSwitch)

        val preferences = requireContext().getSharedPreferences(
            "CampgroundSettings",
            Context.MODE_PRIVATE
        )

        val darkModeEnabled =
            preferences.getBoolean("dark_mode", false)

        darkModeSwitch.isChecked = darkModeEnabled

        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->

            preferences.edit()
                .putBoolean("dark_mode", isChecked)
                .apply()

            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) {
                    AppCompatDelegate.MODE_NIGHT_YES
                } else {
                    AppCompatDelegate.MODE_NIGHT_NO
                }
            )
        }
    }
}
