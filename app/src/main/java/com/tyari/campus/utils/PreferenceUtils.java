package com.tyari.campus.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.Set;

public class PreferenceUtils {
    public static final String KEY_LANG = "KEY_LANG";
    public static final String KEY_USER = "KEY_USER";
    public static final String KEY_SELECTED_SUBJECTS = "KEY_SELECTED_SUBJECTS";

    private static final String TAG = "PreferenceUtils";
    private static final String SHARED_PREFS_FILE = "user_prefs";
    private static PreferenceUtils sInstance;
    private SharedPreferences mSharedPreferences;

    private PreferenceUtils(Context context) {
        if (mSharedPreferences == null) {
            mSharedPreferences = context.getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        }
    }

    public static synchronized PreferenceUtils getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new PreferenceUtils(context);
        }
        return sInstance;
    }

    public Map<String, ?> getAllSharedPreferences() {
        return mSharedPreferences.getAll();
    }


    public boolean getBoolean(String key) {
        return mSharedPreferences.getBoolean(key, false);
    }

    public void putBoolean(String key, boolean value) {
        Editor editor = mSharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public float getFloat(String key) {
        return mSharedPreferences.getFloat(key, 0);
    }

    public void putFloat(String key, float value) {
        Editor editor = mSharedPreferences.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public int getInt(String key) {
        return mSharedPreferences.getInt(key, 0);
    }

    public void putInt(String key, int value) {
        Editor editor = mSharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public long getLong(String key) {
        return mSharedPreferences.getLong(key, 0);
    }

    public void putLong(String key, long value) {
        Editor editor = mSharedPreferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public String getString(String key) {
        return mSharedPreferences.getString(key, "");
    }

    public void putString(String key, String value) {
        Editor editor = mSharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public Set<String> getStringSet(String key) {
        return mSharedPreferences.getStringSet(key, null);
    }

    public void putStringSet(String key, Set<String> value) {
        Editor editor = mSharedPreferences.edit();
        editor.putStringSet(key, value);
        editor.apply();
    }

    public void putObject(String key, Object object) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream;
        try {
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            byte[] data = byteArrayOutputStream.toByteArray();
            objectOutputStream.close();
            byteArrayOutputStream.close();
            putString(key, new String(Base64.encode(data, Base64.NO_WRAP)));
        } catch (IOException ex) {
            Log.e(TAG, ex.toString(), ex);
        } catch (Exception ex) {
            Log.e(TAG, ex.toString(), ex);
        }
    }

    public Object getObject(String key) {
        byte[] data = getString(key).getBytes();

        if (data.length == 0) {
            return null;
        }

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Base64.decode(data, Base64.NO_WRAP));
        ObjectInputStream objectInputStream = null;
        Object object = null;
        try {
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
        } catch (Exception ex) {
            Log.e(TAG, ex.toString(), ex);
        }

        try {
            object = objectInputStream.readObject();
        } catch (Exception ex) {
            Log.e(TAG, ex.toString(), ex);
        } finally {
            try {
                if (byteArrayInputStream != null) {
                    byteArrayInputStream.close();
                }
            } catch (IOException ex) {
                Log.e(TAG, ex.toString(), ex);
            }
            try {
                if (objectInputStream != null)
                    objectInputStream.close();
            } catch (IOException ex) {
                Log.e(TAG, ex.toString(), ex);
            }
        }
        return object;
    }

    public void removePreference(String key) {
        if (mSharedPreferences.contains(key)) {
            Editor editor = mSharedPreferences.edit();
            editor.remove(key);
            editor.apply();
        }
    }

    public boolean containsKey(String key) {
        return mSharedPreferences.contains(key);
    }
}
