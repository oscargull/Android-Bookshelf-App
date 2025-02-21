package utils;

import android.content.Context;
import android.util.Log;
import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava2.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava2.RxDataStore;
import io.reactivex.Single;

//Singleton that manages datastore preferences
public class DataStoreManager {

    private RxDataStore<Preferences> dataStore;
    private static DataStoreManager instance;


    public static DataStoreManager getInstance(Context context) {
        if (instance==null){
            Log.d("null instance", "yes");
            instance=new DataStoreManager(context.getApplicationContext());

        }
        return instance;
    }

    private DataStoreManager(Context context){
        dataStore = new RxPreferenceDataStoreBuilder(context,"myDataStore").build();
    }

    public void setDataStore(RxDataStore<Preferences> dataStore) {
        this.dataStore = dataStore;
    }
    public RxDataStore<Preferences> getDataStore() {
        return dataStore;
    }

    public Single<Preferences> putString(String key, String value){
        Preferences.Key<String> pref_key = PreferencesKeys.stringKey(key);
        if (dataStore == null){
            Log.d("dataStore null ", "yes");
        }
        return dataStore.updateDataAsync(preferences -> {
            MutablePreferences mutablePreferences = preferences.toMutablePreferences();
            mutablePreferences.set(pref_key,value);
            return Single.just(mutablePreferences);
        })/*.onErrorReturnItem(null)*/;
    }

    public Single<String> getString(String key){
        return dataStore.data().map(preferences -> preferences.get(PreferencesKeys.stringKey(key))).firstOrError().onErrorReturnItem("null");
    }






}
