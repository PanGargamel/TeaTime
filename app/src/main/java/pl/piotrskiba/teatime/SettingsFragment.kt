package pl.piotrskiba.teatime

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.snackbar.Snackbar

class SettingsFragment : PreferenceFragmentCompat(), OnSharedPreferenceChangeListener {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref_main)

        val sharedPreferences = preferenceScreen.sharedPreferences
        val count = preferenceScreen.preferenceCount

        for (i in 0 until count) {
            val p = preferenceScreen.getPreference(i)

            if (p is ListPreference) {
                val value = sharedPreferences.getString(p.getKey(), getString(R.string.pref_language_value_default))
                setPreferenceSummary(p, value)
            }
        }
    }

    private fun setPreferenceSummary(preference: Preference, value: String?) {
        if (preference is ListPreference) {
            val preferenceIndex = preference.findIndexOfValue(value)

            if (preferenceIndex >= 0) {
                preference.summary = preference.entries[preferenceIndex]
            }
        } else {
            throw UnsupportedOperationException("Not implemented yet.")
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        val preference = findPreference<Preference>(key)

        if (preference is ListPreference) {
            val value = sharedPreferences.getString(preference.getKey(), getString(R.string.pref_language_value_default))
            setPreferenceSummary(preference, value)
        }
        if (key == getString(R.string.pref_language_key)) {
            Snackbar.make(view!!, getString(R.string.language_changed), Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }
}