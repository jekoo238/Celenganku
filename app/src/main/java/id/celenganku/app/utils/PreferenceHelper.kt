package id.celenganku.app.utils

import android.content.Context
import androidx.preference.PreferenceManager

class PreferenceHelper(context: Context) {

    private val preference = PreferenceManager.getDefaultSharedPreferences(context)

    var theme: Int
        get() = preference.getInt(KEY_THEME, FOLLOW_SYSTEM)
        set(value) {
            preference.edit().putInt(KEY_THEME, value).apply()
        }

    companion object{
        const val KEY_THEME = "theme"
        const val FOLLOW_SYSTEM = 0
        const val LIGHT = 1
        const val DARK = 2
    }

}