package pl.piotrskiba.teatime;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import com.google.android.material.snackbar.Snackbar;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_main);

        PreferenceScreen preferenceScreen = getPreferenceScreen();
        SharedPreferences sharedPreferences = preferenceScreen.getSharedPreferences();

        int count = preferenceScreen.getPreferenceCount();

        for(int i = 0; i < count; i++){
            Preference p = preferenceScreen.getPreference(i);
            if(p instanceof ListPreference) {
                String value = sharedPreferences.getString(p.getKey(), getString(R.string.pref_language_value_default));
                setPreferenceSummary(p, value);
            }
        }
    }

    private void setPreferenceSummary(Preference preference, String value){
        if(preference instanceof ListPreference){
            ListPreference listPreference = (ListPreference) preference;

            int preferenceIndex = listPreference.findIndexOfValue(value);
            if(preferenceIndex >= 0){
                listPreference.setSummary(listPreference.getEntries()[preferenceIndex]);
            }
        }
        else{
            throw new UnsupportedOperationException("Not implemented yet.");
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        if(preference instanceof ListPreference){
            String value = sharedPreferences.getString(preference.getKey(), getString(R.string.pref_language_value_default));
            setPreferenceSummary(preference, value);
        }

        if(key.equals(getString(R.string.pref_language_key))) {
            Snackbar.make(getView(), getString(R.string.language_changed), Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
