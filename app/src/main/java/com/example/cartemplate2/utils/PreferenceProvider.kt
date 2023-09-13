package com.example.cartemplate2.utils

import android.content.Context


class PreferenceProvider(context: Context) {

    private val appContext = context.applicationContext

    private val file = "com.example.askcar.utils"

    private var sharedPreferences = appContext.getSharedPreferences(file, Context.MODE_PRIVATE)
    private val editor = sharedPreferences.edit()

    fun clear() {
        editor.clear()
        editor.commit()
    }

    fun setStringValue(stringData: String, KEY: String) {
        editor.putString(KEY, stringData)
        editor.apply()
    }

    fun getStringValue(KEY: String): String? {
        return sharedPreferences.getString(KEY, "0")
    }

    fun setIntValue(intData: Int, KEY: String) {
        editor.putInt(KEY, intData)
        editor.apply()
    }

    fun getIntValue(KEY: String): Int {
        return sharedPreferences.getInt(KEY, 0)
    }

    fun setBooleanValue(booleanData: Boolean, KEY: String) {
        editor.putBoolean(KEY, booleanData)
        editor.apply()
    }

    fun getBooleanValue(KEY: String): Boolean {
        return sharedPreferences.getBoolean(KEY, false)
    }


    fun setLongValue(intData: Long, KEY: String) {
        editor.putLong(KEY, intData)
        editor.apply()
    }

    fun getLongValue(KEY: String): Long {
        return sharedPreferences.getLong(KEY, 0)
    }


}