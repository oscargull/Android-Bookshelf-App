package com.example.bookshelfapp.utils;

import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.preference.PreferenceDataStore;
import io.reactivex.Flowable;

//Override PreferenceDataStore to make it work with PreferenceScreen
public class CustomPreferenceDataStore extends PreferenceDataStore {

    private DataStoreManager dataStoreManager;

    public CustomPreferenceDataStore(DataStoreManager dataStoreManager) {
        this.dataStoreManager = dataStoreManager;
    }

    public Flowable<Preferences> observeChanges(){
        return dataStoreManager.observeDataChanges();
    }

    @Override
    public void putString(String key, String value) {
        dataStoreManager.putString(key, value);
    }

    @Override
    public String getString(String key, String defValue) {
        String storedValue = dataStoreManager.getString(key).blockingGet();
        return storedValue != null ? storedValue : defValue;
    }

    @Override
    public void putBoolean(String key, boolean value) {
        dataStoreManager.putBoolean(key, value);
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        boolean storedValue = dataStoreManager.getBoolean(key).blockingGet();
        return storedValue != false ? storedValue : defValue;
    }

    public Flowable<Boolean> observeBoolean(String key){
        return  dataStoreManager.observePreference(PreferencesKeys.booleanKey(key), false);
    }

    @Override
    public void putInt(String key, int value) {
        dataStoreManager.putInt(key, value);
    }

    @Override
    public int getInt(String key, int defValue) {
        Integer storedValue = dataStoreManager.getInt(key).blockingGet();
        return storedValue != null ? storedValue : defValue;
    }

    @Override
    public void putFloat(String key, float value) {
        dataStoreManager.putFloat(key, value);
    }

    @Override
    public float getFloat(String key, float defValue) {
        Float storedValue = dataStoreManager.getFloat(key).blockingGet();
        return storedValue != null ? storedValue : defValue;
    }

    @Override
    public void putLong(String key, long value) {
        throw new UnsupportedOperationException("putLong not implemented on DataStoreManager");
    }

    @Override
    public long getLong(String key, long defValue) {
        throw new UnsupportedOperationException("getLong not implemented on DataStoreManager");
    }
}
