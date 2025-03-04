package com.example.bookshelfapp.utils;

import android.content.Context;
import android.util.Log;
import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava2.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava2.RxDataStore;
import io.reactivex.Flowable;
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

    public Flowable<Preferences> observeDataChanges(){
        return dataStore.data();
    }

    //Flowable to subscirbe to changes in DataStore
    public <T> Flowable<T> observePreference(Preferences.Key<T> key, T defaultValue) {
        return dataStore.data().map(preferences -> preferences.get(key) != null ? preferences.get(key) : defaultValue);
    }

    //Remove a key
    public <T> Single<Preferences> removeKey(Preferences.Key<T> prefKey) {
        return dataStore.updateDataAsync(preferences -> {
            MutablePreferences mutablePreferences = preferences.toMutablePreferences();
            mutablePreferences.remove(prefKey);
            return Single.just(mutablePreferences);
        });
    }


    //Put and get methods

    public Single<Preferences> putString(String key, String value){
        Preferences.Key<String> pref_key = PreferencesKeys.stringKey(key);
        return dataStore.updateDataAsync(preferences -> {
            MutablePreferences mutablePreferences = preferences.toMutablePreferences();
            mutablePreferences.set(pref_key,value);
            return Single.just(mutablePreferences);
        });
    }

    public Single<String> getString(String key){
        return dataStore.data().map(preferences -> preferences.get(PreferencesKeys.stringKey(key))).firstOrError().onErrorReturnItem("null");
    }

    public Single<Preferences> putBoolean(String key, boolean value){
        Preferences.Key<Boolean> pref_key = PreferencesKeys.booleanKey(key);
        return dataStore.updateDataAsync(preferences -> {
            MutablePreferences mutablePreferences = preferences.toMutablePreferences();
            mutablePreferences.set(pref_key,value);
            return Single.just(mutablePreferences);
        });
    }

    public Single<Boolean> getBoolean(String key){
        return dataStore.data().map(preferences -> preferences.get(PreferencesKeys.booleanKey(key))).firstOrError().onErrorReturnItem(false);
    }

    public Single<Preferences> putInt(String key, int value){
        Preferences.Key<Integer> pref_key = PreferencesKeys.intKey(key);
        return dataStore.updateDataAsync(preferences -> {
            MutablePreferences mutablePreferences = preferences.toMutablePreferences();
            mutablePreferences.set(pref_key,value);
            return Single.just(mutablePreferences);
        });
    }

    public Single<Integer> getInt(String key){
        return dataStore.data().map(preferences -> preferences.get(PreferencesKeys.intKey(key))).firstOrError().onErrorReturnItem(null);
    }

    public Single<Preferences> putFloat(String key, float value){
        Preferences.Key<Float> pref_key = PreferencesKeys.floatKey(key);
        return dataStore.updateDataAsync(preferences -> {
            MutablePreferences mutablePreferences = preferences.toMutablePreferences();
            mutablePreferences.set(pref_key,value);
            return Single.just(mutablePreferences);
        });
    }

    public Single<Float> getFloat(String key){
        return dataStore.data().map(preferences -> preferences.get(PreferencesKeys.floatKey(key))).firstOrError().onErrorReturnItem(null);
    }







}
