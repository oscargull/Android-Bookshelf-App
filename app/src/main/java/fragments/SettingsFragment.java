package fragments;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.example.bookshelfapp.R;
import utils.CustomPreferenceDataStore;
import utils.DataStoreManager;


public class SettingsFragment extends PreferenceFragmentCompat {

    private CustomPreferenceDataStore myPreferenceDataStore;

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        myPreferenceDataStore = new CustomPreferenceDataStore(DataStoreManager.getInstance(requireContext()));
        setPreferencesFromResource(R.xml.settings,rootKey);

        PreferenceScreen screen = getPreferenceScreen();
        if (screen != null) {
            screen.setPreferenceDataStore(myPreferenceDataStore);
        }


        //Manually handling each preference since SharedPreferences isn't compatible with default PreferenceDataStore

        SwitchPreference prefKeepLoggedIn = findPreference("keep_logged_in");
        SwitchPreference prefDarkMode = findPreference("dark_mode");

        if (prefKeepLoggedIn != null){
            myPreferenceDataStore.observeBoolean("keep_logged_in").subscribe(keepLoggedIn -> {
               prefKeepLoggedIn.setChecked(keepLoggedIn);
//                requireActivity().runOnUiThread(() -> {
//                    Toast.makeText(requireContext(), "keep logged datastore: " + keepLoggedIn, Toast.LENGTH_SHORT).show();
//                });
            }, throwable -> {
                Log.e("SettingsFragment", "Error observing keep_logged_in", throwable);
            });

            prefKeepLoggedIn.setOnPreferenceChangeListener((preference, newValue) -> {
                myPreferenceDataStore.putBoolean("keep_logged_in", (Boolean)newValue);
//                Log.d("keep logged pref: ",String.valueOf((Boolean)newValue));
                return true;
            });
        }

        if (prefDarkMode != null){
            myPreferenceDataStore.observeBoolean("dark_mode").subscribe(darkMode -> {
                prefDarkMode.setChecked(darkMode);
//                requireActivity().runOnUiThread(() -> {
//                    Toast.makeText(requireContext(), "dark_mode datastore: " + darkMode, Toast.LENGTH_SHORT).show();
//                });
            }, throwable -> {
                Log.e("SettingsFragment", "Error observing dark_mode", throwable);
            });

            prefDarkMode.setOnPreferenceChangeListener((preference, newValue) -> {
                myPreferenceDataStore.putBoolean("dark_mode", (Boolean)newValue);
//                Log.d("dark mode pref: ",String.valueOf((Boolean)newValue));
                return true;
            });
        }
    }



}
