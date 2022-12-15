package id.celenganku.app.utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val theme = intPreferencesKey("theme")

fun Preferences?.currentTheme(): Int = this?.get(theme) ?: 0

fun MutablePreferences.setCurrentTheme(newTheme: Int) {
    this[theme] = newTheme
}

fun DataStore<Preferences>.getData(scope: CoroutineScope, result: (Preferences?) -> Unit) {
    scope.launch(Dispatchers.IO) {
        val pref = data.firstOrNull()
        withContext(Dispatchers.Main) {
            result(pref)
        }
    }
}

fun DataStore<Preferences>.editData(scope: CoroutineScope, result: (MutablePreferences) -> Unit) {
    scope.launch(Dispatchers.IO) {
        edit {
            result(it)
        }
    }
}