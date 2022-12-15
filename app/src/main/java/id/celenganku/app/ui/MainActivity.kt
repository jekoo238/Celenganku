package id.celenganku.app.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import id.celenganku.app.databinding.ActivityMainBinding
import id.celenganku.app.utils.changeTheme
import id.celenganku.app.utils.currentTheme
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private val dataStore: DataStore<Preferences> by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        dataStore.data.asLiveData(lifecycleScope.coroutineContext).observe(this) {
            changeTheme(it.currentTheme())
        }
        setContentView(ActivityMainBinding.inflate(layoutInflater).root)
    }
}